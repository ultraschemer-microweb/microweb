<?php
  require_once './common_credentials.php';

  if($_GET['native'] == 'true') {
    $uri = $redirect_uri_native;
  } else {
    $uri = $redirect_uri_web;
  }

  header('Location: ' . $openid_server_address . '/auth/realms/' . $realm . '/protocol/openid-connect/auth?scope=uma_protection&response_type=code&client_id=' . $client_id . '&redirect_uri=' . urlencode($uri) . '&state=' . urlencode($_GET['state']));