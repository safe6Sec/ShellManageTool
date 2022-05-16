function requestByCurl(){

    $httpUrl=get("httpUrl");
    $httpUri=get("httpUri");
    $httpMehtod=get("httpMehtod");
    $httpHeaders=get("httpHeaders");
    $httpPort=get("httpPort");
    $httpRequestData=get("httpRequestData");

    $headerArray = explode("\r\n",$httpHeaders);
    $curl = curl_init();
    curl_setopt($curl, CURLOPT_URL, $httpUrl);
    curl_setopt($curl,CURLOPT_HEADER,1);
    curl_setopt($curl, CURLOPT_RETURNTRANSFER, 1);
    curl_setopt($curl, CURLOPT_SSL_VERIFYPEER, false);
    curl_setopt($curl, CURLOPT_SSL_VERIFYHOST,false);
    curl_setopt($curl, CURLOPT_CUSTOMREQUEST,$httpMehtod);
    curl_setopt($curl,CURLOPT_HTTPHEADER,$headerArray);
    if ($httpRequestData!=null){
        curl_setopt($curl, CURLOPT_POSTFIELDS, $httpRequestData);
    }
    $output = curl_exec($curl);
    curl_close($curl);
    return $output;
}
function requestByFsockopen()
{
    $httpUrl=get("httpUrl");
    $httpUri=get("httpUri");
    $httpMehtod=get("httpMehtod");
    $httpHeaders=get("httpHeaders");
    $httpPort=get("httpPort");
    $httpRequestData=get("httpRequestData");

    $url=$httpUrl;
    $url = parse_url($url);
    if (empty ($url ['scheme']) || $url ['scheme'] != 'http') {
        return "Error: Only HTTP request are supported !";
    }
    $host = $url ['host'];
    $path = isset ($url ['path']) ? $url ['path'] : '/';
    if (!isset($port)){
        $port=80;
    }else{
        $port = $url ['port'];
    }
    $fp = fsockopen($host, $httpPort, $errno, $errstr, 3);
    if ($fp) {
        if (function_existsEx("stream_set_blocking")){
            @stream_set_blocking($fp, 0);
        }
        if (function_existsEx("stream_set_timeout")){
            @stream_set_timeout($fp,1000);
        }
        $request = <<<HEADER
{$httpMehtod} {$httpUri} HTTP/1.1
{$httpHeaders}

{$httpRequestData}
HEADER;
        fwrite($fp, $request);
        $result = '';
        while (!feof($fp)) {
            $result .= fread($fp, 1024*1024);
        }
    } else {
        return "no connect!";
    }
    @fclose($fp);
    return $result;
}
function requestByStream()
{
    $httpUrl=get("httpUrl");
    $httpUri=get("httpUri");
    $httpMehtod=get("httpMehtod");
    $httpHeaders=get("httpHeaders");
    $httpPort=get("httpPort");
    $httpRequestData=get("httpRequestData");

    $opts = array('http' =>
        array(
            'method' => $httpMehtod,
            'header' => $httpHeaders,
            'content' => $httpRequestData
        )
    );
    $context = stream_context_create($opts);
    $result = file_get_contents($httpUrl, false, $context);

    $response=implode("\r\n",$http_response_header);

    $response.="\r\n";
    $response.="\r\n";


    $response.=$result;

    return $response;
}

if (function_exists("curl_init")){
    return requestByCurl();
}elseif (function_exists("fsockopen")){
    return requestByFsockopen();
}elseif (function_exists('stream_context_create')){
    return requestByStream();
}else{
    return "Not Found Function";
}
