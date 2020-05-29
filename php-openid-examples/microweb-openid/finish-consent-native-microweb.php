<?php
  require_once './common_credentials.php'; 



  // Request form data:
  $data = array (
    // Parameters gotten from redirected query string:
    'state' => $raw_state,
    'session_state' => $_GET['session_state'],
    'code' => $_GET['code'],

    // Below parameters must be gotten from backend configuration or database:
    'redirect_uri' => $redirect_uri_native,
    'client_secret' => $client_secret,
    'client_id' => $client_id
  );

  $params = '';
  foreach($data as $key=>$value) {
    $params .= $key.'='.$value.'&';
  }
  $params = trim($params, '&');

  // Make the Microweb backend call, to get the Access Token and all other session information:
  $url = $server_backend_resource . '/v1/finish-consent';
  $ch = curl_init();
  curl_setopt($ch, CURLOPT_URL, $url.'?'.$params ); //Url together with parameters
  curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); //Return data instead printing directly in Browser
  curl_setopt($ch, CURLOPT_CONNECTTIMEOUT , 7); //Timeout after 7 seconds
  curl_setopt($ch, CURLOPT_USERAGENT , "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
  curl_setopt($ch, CURLOPT_HEADER, 0);

  $result = curl_exec($ch);

  // Format response:
  header('Content-Type: text/html; charset=utf-8');
  if(curl_errno($ch))  { //catch if curl error exists and show it
    $obj = new stdClass();
    $obj->error = curl_error($ch);
    ?>
      <html>
        Communication error with backend.
      </html>
    <?php
  } else { 
    // echo $result;
    ?>
      <html>
        <head>
          <script language="javascript">
            window.onload = function () {
              if(window.ReactNativeWebView) { 
                window.ReactNativeWebView.postMessage(JSON.stringify(<?php echo $result; ?>))
              } else {
                window.parent.postMessage(JSON.stringify(<?php echo $result; ?>), "*")
              }
            }
          </script>
        </head>
        <body>
        </body>
      </html>
    <?php
  }

  // Close the curl object:
  curl_close($ch);
?>
