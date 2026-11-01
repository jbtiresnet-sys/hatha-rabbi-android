<?php
/**
 * Plugin Name: This Is My God - App Manager
 * Description: Public app configuration and WordPress category visibility controls.
 * Version: 0.2.0
 * Requires PHP: 7.4
 */
if (!defined('ABSPATH')) exit;
add_action('admin_menu', function () {
 add_options_page('إعدادات تطبيق هذا ربي', 'تطبيق هذا ربي', 'manage_options', 'hatha-rabbi-app', 'hatha_rabbi_settings');
});
add_action('admin_init', function () {
 register_setting('hatha_rabbi_group', 'hatha_rabbi_app', [
  'type' => 'array', 'default' => [],
  'sanitize_callback' => function ($data) {
   return [
    'latest_count' => min(50, max(1, absint($data['latest_count'] ?? 20))),
    'accent_color' => sanitize_hex_color($data['accent_color'] ?? '#377a7d') ?: '#377a7d',
    'hidden_categories' => array_values(array_map('absint', (array)($data['hidden_categories'] ?? []))),
   ];
  }
 ]);
});
function hatha_rabbi_settings() {
 if (!current_user_can('manage_options')) return;
 $settings = get_option('hatha_rabbi_app', []);
 echo '<div class="wrap"><h1>إعدادات تطبيق هذا ربي</h1><form method="post" action="options.php">';
 settings_fields('hatha_rabbi_group');
 echo '<p>عدد أحدث المقالات <input type="number" min="1" max="50" name="hatha_rabbi_app[latest_count]" value="'.esc_attr($settings['latest_count'] ?? 20).'"></p>';
 echo '<p>لون الواجهة <input type="color" name="hatha_rabbi_app[accent_color]" value="'.esc_attr($settings['accent_color'] ?? '#377a7d').'"></p>';
 echo '<h2>إخفاء تصنيفات من التطبيق</h2>';
 foreach (get_categories(['hide_empty' => false]) as $cat) {
  $checked = in_array($cat->term_id, (array)($settings['hidden_categories'] ?? []), true);
  echo '<p><label><input type="checkbox" name="hatha_rabbi_app[hidden_categories][]" value="'.esc_attr($cat->term_id).'" '.checked($checked,true,false).'> '.esc_html($cat->name).'</label></p>';
 }
 submit_button(); echo '</form></div>';
}
add_action('rest_api_init', function () {
 register_rest_route('hatha-rabbi/v1', '/config', [
  'methods' => 'GET', 'permission_callback' => '__return_true',
  'callback' => function () {
   $s = get_option('hatha_rabbi_app', []);
   return rest_ensure_response([
    'latest_count' => (int)($s['latest_count'] ?? 20),
    'accent_color' => $s['accent_color'] ?? '#377a7d',
    'hidden_categories' => array_values(array_map('absint',(array)($s['hidden_categories'] ?? []))),
   ]);
  }
 ]);
});

require_once __DIR__ . '/notifications.php';
