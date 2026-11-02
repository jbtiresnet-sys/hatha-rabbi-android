<?php
if (!defined('ABSPATH')) exit;

/**
 * Server-side FCM HTTP v1 integration.
 * Configure HATHA_RABBI_FCM_SERVICE_ACCOUNT_JSON in wp-config.php.
 * Never place a service-account private key in a public GitHub repository.
 */
function hatha_rabbi_fcm_credentials() {
 if (!defined('HATHA_RABBI_FCM_SERVICE_ACCOUNT_JSON')) return new WP_Error('missing_credentials', 'يجب إعداد بيانات حساب خدمة Firebase على الخادم.');
 $value = HATHA_RABBI_FCM_SERVICE_ACCOUNT_JSON;
 if (is_array($value)) $data = $value;
 else $data = json_decode($value, true);
 if (!is_array($data) || empty($data['client_email']) || empty($data['private_key']) || empty($data['project_id'])) return new WP_Error('invalid_credentials', 'بيانات حساب خدمة Firebase غير صحيحة.');
 return $data;
}
function hatha_rabbi_fcm_token() {
 $cached = get_transient('hatha_rabbi_fcm_access_token');
 if ($cached) return $cached;
 $cred = hatha_rabbi_fcm_credentials();
 if (is_wp_error($cred)) return $cred;
 $now = time();
 $b64 = function ($data) { return rtrim(strtr(base64_encode(wp_json_encode($data)), '+/', '-_'), '='); };
 $header = $b64(['alg'=>'RS256','typ'=>'JWT']);
 $claims = $b64(['iss'=>$cred['client_email'],'scope'=>'https://www.googleapis.com/auth/firebase.messaging','aud'=>'https://oauth2.googleapis.com/token','iat'=>$now,'exp'=>$now+3600]);
 $unsigned = $header.'.'.$claims;
 if (!openssl_sign($unsigned, $signature, $cred['private_key'], OPENSSL_ALGO_SHA256)) return new WP_Error('jwt_sign_failed', 'تعذر توقيع طلب Firebase.');
 $jwt = $unsigned.'.'.rtrim(strtr(base64_encode($signature), '+/', '-_'), '=');
 $response = wp_remote_post('https://oauth2.googleapis.com/token', ['timeout'=>20,'body'=>['grant_type'=>'urn:ietf:params:oauth:grant-type:jwt-bearer','assertion'=>$jwt]]);
 if (is_wp_error($response)) return $response;
 $data = json_decode(wp_remote_retrieve_body($response), true);
 if (wp_remote_retrieve_response_code($response) !== 200 || empty($data['access_token'])) return new WP_Error('oauth_failed', 'تعذر الحصول على تصريح Firebase.');
 set_transient('hatha_rabbi_fcm_access_token', $data['access_token'], max(60, (int)($data['expires_in'] ?? 3600)-120));
 return $data['access_token'];
}
function hatha_rabbi_send_push($title, $body, $url = '') {
 $token = hatha_rabbi_fcm_token();
 if (is_wp_error($token)) return $token;
 $cred = hatha_rabbi_fcm_credentials();
 if (is_wp_error($cred)) return $cred;
 $title = sanitize_text_field($title);
 $body = sanitize_textarea_field($body);
 $data = ['title'=>$title,'body'=>$body];
 if ($url && wp_http_validate_url($url) && wp_parse_url($url, PHP_URL_HOST) === wp_parse_url(home_url(), PHP_URL_HOST)) $data['url'] = esc_url_raw($url);
 $payload = ['message'=>['topic'=>'all_users','notification'=>['title'=>$title,'body'=>$body],'data'=>$data,'android'=>['notification'=>['channel_id'=>'new_content']]]];
 $endpoint = 'https://fcm.googleapis.com/v1/projects/'.rawurlencode($cred['project_id']).'/messages:send';
 $response = wp_remote_post($endpoint, ['timeout'=>20,'headers'=>['Authorization'=>'Bearer '.$token,'Content-Type'=>'application/json'],'body'=>wp_json_encode($payload)]);
 if (is_wp_error($response)) return $response;
 if (wp_remote_retrieve_response_code($response) !== 200) return new WP_Error('fcm_failed', 'رفض Firebase إرسال الإشعار: '.wp_remote_retrieve_response_code($response));
 return true;
}
add_action('admin_menu', function () {
 add_submenu_page('options-general.php', 'إشعارات هذا ربي', 'إشعارات هذا ربي', 'manage_options', 'hatha-rabbi-notifications', 'hatha_rabbi_notifications_page');
});
add_action('admin_init', function () {
 register_setting('hatha_rabbi_notifications_group', 'hatha_rabbi_auto_push', ['type'=>'boolean','sanitize_callback'=>'rest_sanitize_boolean','default'=>false]);
});
function hatha_rabbi_notifications_page() {
 if (!current_user_can('manage_options')) return;
 $status = '';
 if (isset($_POST['hatha_rabbi_send'])) {
  check_admin_referer('hatha_rabbi_send_notification');
  $title = sanitize_text_field(wp_unslash($_POST['push_title'] ?? ''));
  $body = sanitize_textarea_field(wp_unslash($_POST['push_body'] ?? ''));
  $url = esc_url_raw(wp_unslash($_POST['push_url'] ?? ''));
  $result = ($title && $body) ? hatha_rabbi_send_push($title,$body,$url) : new WP_Error('empty','العنوان والنص مطلوبان.');
  $status = is_wp_error($result) ? $result->get_error_message() : 'تم قبول الإشعار من Firebase.';
 }
 echo '<div class="wrap"><h1>إشعارات تطبيق هذا ربي</h1>';
 if ($status) echo '<div class="notice notice-info"><p>'.esc_html($status).'</p></div>';
 if (is_wp_error(hatha_rabbi_fcm_credentials())) echo '<div class="notice notice-warning"><p>أضف ثابت HATHA_RABBI_FCM_SERVICE_ACCOUNT_JSON في wp-config.php لتفعيل الإرسال. لا تنشر المفتاح الخاص في GitHub.</p></div>';
 echo '<h2>إرسال إشعار يدوي</h2><form method="post">';
 wp_nonce_field('hatha_rabbi_send_notification');
 echo '<p><label>العنوان<br><input class="regular-text" name="push_title" required></label></p>';
 echo '<p><label>الرسالة<br><textarea class="large-text" rows="4" name="push_body" required></textarea></label></p>';
 echo '<p><label>رابط المحتوى داخل الموقع (اختياري)<br><input type="url" class="large-text" name="push_url" placeholder="'.esc_attr(home_url('/')).'"></label></p>';
 submit_button('إرسال الإشعار', 'primary', 'hatha_rabbi_send');
 echo '</form><hr><h2>الإرسال التلقائي عند النشر</h2><form method="post" action="options.php">';
 settings_fields('hatha_rabbi_notifications_group');
 echo '<label><input type="checkbox" name="hatha_rabbi_auto_push" value="1" '.checked((bool)get_option('hatha_rabbi_auto_push',false),true,false).'> إرسال إشعار عند نشر أي محتوى جديد (مقالات، أخبار، صوتيات، فيديوهات، وجميع أنواع المحتوى العامة)</label>';
 submit_button('حفظ الإعدادات'); echo '</form></div>';
}
add_action('transition_post_status', function ($new, $old, $post) {
 if ($new !== 'publish' || $old === 'publish' || !$post instanceof WP_Post || !get_option('hatha_rabbi_auto_push', false)) return;
 if (wp_is_post_revision($post->ID) || wp_is_post_autosave($post->ID)) return;
 $type = get_post_type_object($post->post_type);
 if (!$type || !$type->public || in_array($post->post_type, ['attachment'], true) || get_post_meta($post->ID, '_hatha_rabbi_push_sent', true)) return;
 $title = wp_strip_all_tags(get_the_title($post));
 $excerpt = wp_trim_words(wp_strip_all_tags(strip_shortcodes($post->post_excerpt ?: $post->post_content)), 24, '…');
 $result = hatha_rabbi_send_push('جديد في هذا ربي: '.$title, $excerpt ?: 'اضغط لقراءة المحتوى الجديد', get_permalink($post));
 if (is_wp_error($result)) error_log('Hatha Rabbi FCM: '.$result->get_error_code());
 else update_post_meta($post->ID, '_hatha_rabbi_push_sent', current_time('mysql'));
}, 10, 3);
