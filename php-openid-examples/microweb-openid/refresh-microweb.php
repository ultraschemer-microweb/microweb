<?php
  require_once './common_credentials.php'; 
  
  // Request form data:
  $data = array (
    'client_id' => $client_id,
    'client_secret' => $client_secret,
    'refresh_token' => $_POST['refresh_token'],
    'grant_type' => 'refresh_token'
  );

  $params = '';
  foreach($data as $key=>$value) {
    $params .= $key.'='.$value.'&';
  }
  $params = trim($params, '&');

  // Make the Microweb backend call, to get the Access Token and all other session information:
  $url = $openid_server_address . '/auth/realms/' . $realm . '/protocol/openid-connect/token';
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $url);
  curl_setopt($ch, CURLOPT_POST, 1);
  curl_setopt($ch, CURLOPT_POSTFIELDS, $params);
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Return data instead printing directly in Browser
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT , 7); //Timeout after 7 seconds
  curl_setopt($ch, CURLOPT_USERAGENT , "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
  curl_setopt($ch, CURLOPT_HEADER, 0);

  $result = curl_exec($ch);

  // Format response:
  header('Content-Type: application/json; charset=utf-8');
  if(curl_errno($ch)) { //catch if curl error exists and show it
    http_response_code(500);
	echo json_encode(array('error' => curl_error($ch)));
  } else { 
    $info = curl_getinfo($ch);
    http_response_code($info['http_code']);
    if($info['http_code'] != 204) {
      echo json_encode(array('result' => json_decode($result), 'info' => $info));
    }
  }

  // Close the curl object:
  curl_close($ch);
?>
