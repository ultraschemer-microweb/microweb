<?php
  // Set CORS policy - Development only - do not enabled on homologation or production:
  header('Access-Control-Allow-Methods: POST, OPTIONS');
  header('Access-Control-Allow-Headers: A-IM, Accept, Accept-Charset, Accept-Datetime, Accept-Encoding, Accept-Language, Access-Control-Request-Method, Access-Control-Request-Headers, Authorization, Cache-Control, Connection, Content-Length, Content-MD5, Content-Type, Cookie, Date, Expect, Forwarded, From, Host, HTTP2-Settings, If-Match, If-Modified-Since, If-None-Match, If-Range, If-Unmodified-Since, Max-Forwards, Origin, Pragma, Proxy-Authorization, Range, Referer, TE, Trailer, Transfer-Encoding, User-Agent, Upgrade, Via, Warning, Access-Control-Allow-Origin, Upgrade-Insecure-Requests, X-Requested-With, DNT, X-Forwarded-For, X-Forwarded-Host, X-Forwarded-Proto, Front-End-Https, X-Http-Method-Override, X-ATT-DeviceId, X-Wap-Profile, Proxy-Connection, X-UIDH, X-Csrf-Token, X-Request-ID, X-Correlation-ID, Save-Data');
  header('Access-Control-Allow-Origin: *');

  // Iframe-embedding - development:
  header('X-Frame-Options: ALLOWALL');
  header('Content-Security-Policy: frame-ancestors *');

  // Iframe-embedding - production and homologation:
  // header('X-Frame-Options: SAMEORIGIN');
  // header('Content-Security-Policy: frame-ancestors self');

  // Interrupt on "options" method - Development only - do not enable on homologation or production:
  if($_SERVER['REQUEST_METHOD'] === 'OPTIONS') {
    exit();
  }

  /////////////////////////////////////////////////////////////////////////////
  // OpenID access configurations: ////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////

  // Client credentials (can't be loaded from client side):
  $client_secret = '<<client-secret>>';
  $client_id = 'microweb';
   
  // Other parameters (can be loaded and reconfigured from client side):
  // openid-server is the server where KeyCloak is accessible at https://<openid-server>/auth
  
  // Default values:
  $openid_server_address = 'http://<<main-openid-server>>'; 
  $redirect_uri_web = 'http://<<redirect-openid-server>>/microweb-openid/finish-consent-microweb.php';
  $redirect_uri_native = 'http://<<redirect-openid-server>>/microweb-openid/finish-consent-native-microweb.php';
  $redirect_uri_test = urlencode('/s/finish-consent.php'); /* Also can be: 'https://<openid-server?>/s/finish-consent.php'; */
  $server_backend_resource = $openid_server_address . '/microweb';
  $realm = '<<realm>>';
  $window_location_href = '<<web page to be redirected after login>>' 

  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////
  /////////////////////////////////////////////////////////////////////////////

  if(isset($_GET['state'])) {
    $raw_state = $_GET['state'];
  } else {
    $raw_state = "{}";
  }

  // Setting the configured values for "Other parameters", from Query String parameters:
  if(isset($_GET["open_id_server_address"])) {
    $openid_server_address = $_GET["open_id_server_address"]; 
  }

  if(isset($_GET["redirect_uri_web"])) {
    $redirect_uri_web = $_GET["redirect_uri_web"];
  }

  if(isset($_GET["redirect_uri_native"])) {
    $redirect_uri_native = $_GET["redirect_uri_native"];
  }
  
  if(isset($_GET["redirect_uri_test"])) {
    $redirect_uri_test = $_GET["redirect_uri_test"];
  } 
  
  if(isset($_GET["server_backend_resource"])) {
    $server_backend_resource = $_GET["server_backend_resource"];
  } 
  
  if(isset($_GET["realm"])) {
    $realm = $_GET["realm"];

    // You can provide customizations based on received parameters:
    if($realm === '<another-realm>') {
      // Obviously, if the realm is different from <realm>, the secret will be different too,
      // and it's is saved here, since the client_secret and the client_id NEVER can
      // be accessible to the client!
      $client_secret = '1456f89b-fca2-491f-8ce7-a0a742ca2c12';
    }
  }
  
  // Setting the configured values for "Other parameters", from state variable:
  if(isset($_GET['state'])) {
    $state_info = json_decode($_GET['state'], true);
    
    if(isset( $state_info["oisa"])) {
      $openid_server_address = $state_info["oisa"]; 
    }
  
    if(isset($state_info["ruw"])) {
      $redirect_uri_web = $state_info["ruw"];
    }
  
    if(isset($state_info["run"])) {
      $redirect_uri_native = $state_info["run"];
    }
    
    if(isset($state_info["rut"])) {
      $redirect_uri_test = $state_info["rut"];
    } 
    
    if(isset($state_info["sbr"])) {
      $server_backend_resource = $state_info["sbr"];
    } 
    
    if(isset($state_info["r"])) {
      $realm = $state_info["r"];
  
      // You can provide customizations based on received parameters:
      if($realm === '<another-realm>') {
        // Obviously, if the realm is different from <realm>, the secret will be different too,
        // and it's is saved here, since the client_secret and the client_id NEVER can
        // be accessible to the client!
        $client_secret = '1456f89b-fca2-491f-8ce7-a0a742ca2c12';
      }
    }
  }
?>
