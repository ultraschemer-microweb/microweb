<?php
  require_once './common_credentials.php'; 
  
  // Usage:
  // Call: http://<openid-server>/auth/realms/<realm>/protocol/openid-connect/auth?scope=uma_protection&response_type=code&client_id=microweb&redirect_uri=%2Fs%2Ffinish-consent.php&state=000001
  // (see common_credentials.php for more information)

  //
  // Prepare parameters:
  //
  $data = array (
    // Parameters gotten from redirected query string:
    'state' => $_GET['state'],
    'session_state' => $_GET['session_state'],
    'code' => $_GET['code'],

    // Below parameters must be gotten from backend configuration or database:
    'redirect_uri' => $redirect_uri_test,
    'client_secret' => $client_secret,
    'client_id' => $client_id
  );

  $params = '';
  foreach($data as $key=>$value) {
    $params .= $key.'='.$value.'&';
  }
  $params = trim($params, '&');

  // Make the MicroWeb backend call, to get the Access Token and all other session information:
  $url = $server_backend_resource . '/v1/finish-consent';
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $url.'?'.$params ); //Url together with parameters
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Return data instead printing directly in Browser
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT , 7); //Timeout after 7 seconds
  curl_setopt($ch, CURLOPT_USERAGENT , "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
  curl_setopt($ch, CURLOPT_HEADER, 0);

  $result = curl_exec($ch);

  // Format response:
  header('Content-Type: application/json');
  if(curl_errno($ch))  { //catch if curl error exists and show it
    $obj = new stdClass();
    $obj->error = curl_error($ch);
    echo json_encode($obj);
  } else {
    echo $result;
  }

  // Close the curl object:
  curl_close($ch);
