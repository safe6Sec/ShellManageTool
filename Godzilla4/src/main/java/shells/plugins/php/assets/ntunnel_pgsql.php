$allowTestMenu = true;

header("Content-Type: text/plain; charset=x-user-defined");
error_reporting(0);
set_time_limit(0);

function handlerPostData(){
    $req=getAllParameters();
    foreach ($req as $key => $value) {
        if (get("isUrlDecode")==="true"){
            $_POST[$key]=urldecode($value);
        }else{
            $_POST[$key]=$value;
        }
    }
}
handlerPostData();

function navicatEcho($data){
    global $current_result;
    $current_result=$current_result.$data;
}
function navicatGetEchoAll(){
    global $current_result;
    return $current_result;
}


function phpversion_int()
{
    list($maVer, $miVer, $edVer) = preg_split("(/|\.|-)", phpversion());
    return $maVer*10000 + $miVer*100 + $edVer;
}

if (phpversion_int() < 50300)
{
    set_magic_quotes_runtime(0);
}

function GetLongBinary($num)
{
    return pack("N",$num);
}

function GetShortBinary($num)
{
    return pack("n",$num);
}

function GetDummy($count)
{
    $str = "";
    for($i=0;$i<$count;$i++)
        $str .= "\x00";
    return $str;
}

function GetBlock($val)
{
    $len = strlen($val);
    if( $len < 254 )
        return chr($len).$val;
    else
        return "\xFE".GetLongBinary($len).$val;
}

function EchoHeader($errno)
{
    $str = GetLongBinary(1111);
    $str .= GetShortBinary(202);
    $str .= GetLongBinary($errno);
    $str .= GetDummy(6);
    navicatEcho($str);
}

function EchoConnInfo($conn)
{
    $str = GetBlock(pg_host($conn));
    $protocol = "3";
    $version = "";
    $is_set = false;
    if(function_exists("pg_version")) {
        $v = pg_version($conn);
        if(array_key_exists('protocol', $v)) {
            $protocol = $v['protocol'];
        }
        if(array_key_exists('server', $v)) {
            $version = $v['server'];
            $is_set = true;
        }
    }
    if(!$is_set) {
        $res = pg_query($conn,"select substring(version(), '[0-9]{1,2}\\.[0-9]{1,2}[\\.|0-9|a-z|A-Z]+')");
        if($res) {
            $numfields = pg_num_fields($res);
            if($numfields == 1) {
                $row = pg_fetch_row($res);
                if($row) {
                    $version = $row[0];
                }
            }
            pg_free_result($res);
        }
    }
    $str .= GetBlock($protocol);

    $major = 0;
    $minor = 0;
    $patch_level = 0;
    $version_int = 0;
    if(sscanf($version, "%d.%d.%d", $major, $minor, $patch_level) >= 3) {
        $version_int = $major*10000+$minor*100+$patch_level;
    } else if(sscanf($version, "%d.%d%s", $major, $minor, $patch_level) >= 3) {
        $version_int = $major*10000+$minor*100;
    } else {
        $version_int = 0; // returns 0 such that navicat able to handle the server version
    }
    $str .= GetBlock($version_int);
    navicatEcho($str);
}

function EchoResultSetHeader($errno, $affectrows, $insertid, $numfields, $numrows)
{
    $str = GetLongBinary($errno);
    $str .= GetLongBinary($affectrows);
    $str .= GetLongBinary($insertid);
    $str .= GetLongBinary($numfields);
    $str .= GetLongBinary($numrows);
    $str .= GetDummy(12);
    navicatEcho($str);
}

function EchoFieldsHeader($res, $numfields)
{
    $str = "";
    for( $i = 0; $i < $numfields; $i++ ) {
        $str .= GetBlock(pg_field_name($res, $i));
        $str .= GetBlock("");

        $type = pg_field_type($res, $i);
        $length = pg_field_size($res, $i);
        switch ($type){
            case "boolean":			$type=16;		break;
            case "bytea":			$type=17;		break;
            case "bit":				$type=1560;		break;
            case "varbit":			$type=1562;		break;
            case "char":			$type=18;		break;
            case "name":			$type=19;		break;
            case "int2vector":		$type=22;		break;
            case "oidvector":		$type=30;		break;
            case "int8":			$type=20;		break;
            case "tid":				$type=27;		break;
            case "int2":			$type=21;		break;
            case "int4":			$type=23;		break;
            case "oid":				$type=26;		break;
            case "xid":				$type=28;		break;
            case "cid":				$type=29;		break;
            case "text":			$type=25;		break;
            case "money":			$type=790;		break;
            case "numeric":			$type=1700;		break;
            case "point":			$type=600;		break;
            case "lseg":			$type=601;		break;
            case "path":			$type=602;		break;
            case "box":				$type=603;		break;
            case "polygon":			$type=604;		break;
            case "line":			$type=628;		break;
            case "circle":			$type=718;		break;
            case "float4":			$type=700;		break;
            case "float8":			$type=701;		break;
            case "abstime":			$type=702;		break;
            case "tinterval":		$type=704;		break;
            case "timestamp":		$type=1114;		break;
            case "timestamptz":		$type=1184;		break;
            case "interval":		$type=1186;		break;
            case "timetz":			$type=1266;		break;
            case "unknown":			$type=705;		break;
            case "macaddr":			$type=829;		break;
            case "inet":			$type=869;		break;
            case "cidr":			$type=650;		break;
            case "bpchar":			$type=1042;		break;
            case "varchar":			$type=1043;		break;
            case "date":			$type=1082;		break;
            case "time":			$type=1083;		break;
            case "regproc":			$type=24;		break;
            case "refcursor":		$type=1790;		break;
            case "regprocedure":	$type=2202;		break;
            case "regoper":			$type=2203;		break;
            case "regoperator":		$type=2204;		break;
            case "regclass":		$type=2205;		break;
            case "regtype":			$type=2206;		break;
            default: 0;
        }

        $str .= GetLongBinary($type);
        $str .= GetLongBinary(0);
        $str .= GetLongBinary($length);
    }
    navicatEcho($str);
}

function EchoData($res, $numfields, $numrows)
{
    for ($i=0; $i < $numrows; $i++ ) {
        $str = "";
        $row = pg_fetch_row( $res );
        for ($j=0; $j < $numfields; $j++ ){
            if( is_null($row[$j]) )
                $str .= "\xFF";
            else
                $str .= GetBlock($row[$j]);
        }
        navicatEcho($str);
    }
}

$_POST["q[]"]=array($_POST["q[]"]);

if (phpversion_int() < 40005) {
    EchoHeader(201);
    navicatEcho(GetBlock("unsupported php version"));
    return navicatGetEchoAll();
}

if (phpversion_int() < 40010) {
    global $HTTP_POST_VARS;
    $_POST = &$HTTP_POST_VARS;
}

if (!isset($_POST["actn"]) || !isset($_POST["host"]) || !isset($_POST["port"]) || !isset($_POST["login"])) {
    $testMenu = $allowTestMenu;
    if (!$testMenu){
        EchoHeader(202);
        navicatEcho(GetBlock("invalid parameters"));
        return navicatGetEchoAll();
    }
}

if (!$testMenu){
    if ($_POST["encodeBase64"] == '1') {
        for($i=0;$i<count($_POST["q[]"]);$i++)
            $_POST["q[]"][$i] = base64_decode($_POST["q[]"][$i]);
    }

    if (!function_exists("pg_connect")) {
        EchoHeader(203);
        navicatEcho(GetBlock("PostgresSQL not supported on the server"));
        return navicatGetEchoAll();
    }

    $errno_c = 0;
    if ($_POST["host"] != "")
        $connstr = "host=".$_POST["host"];
    $connstr .= " port=".$_POST["port"];
    if ($_POST["db"] != "")
        $connstr .= " dbname='".$_POST["db"]."'";
    else
        $connstr .= " dbname='template1'";
    $connstr .= " user=".$_POST["login"]." password=".$_POST["password"];
    $conn = pg_connect($connstr);
    if (!$conn) {
        $errno_c = 202;
        $error_c = "Could not allocate new connection";
    }

    EchoHeader($errno_c);
    if ($errno_c > 0) {
        navicatEcho(GetBlock($error_c));
    } elseif ($_POST["actn"] == "C") {
        EchoConnInfo($conn);
    } elseif ($_POST["actn"] == "Q") {
        for ($i=0; $i < count($_POST["q[]"]); $i++) {
            $query = $_POST["q[]"][$i];
            if ($query == "") continue;
            if (phpversion_int() < 50400){
                if(get_magic_quotes_gpc())
                    $query = stripslashes($query);
            }
            $res = pg_query($conn,$query);
            $error = pg_last_error($conn);
            if ($error <> "")
                $errno = 1;
            else
                $errno = 0;
            $affectedrows = pg_affected_rows($res);
            $insertid = 0;
            $numfields = pg_num_fields($res);
            $numrows = pg_num_rows($res);
            EchoResultSetHeader($errno, $affectedrows, $insertid, $numfields, $numrows);
            if($errno > 0)
                navicatEcho(GetBlock($error));
            else {
                if ($numfields > 0) {
                    EchoFieldsHeader($res, $numfields);
                    EchoData($res, $numfields, $numrows);
                } else
                    navicatEcho(GetBlock(""));
            }
            if ($i < (count($_POST["q[]"])-1))
                navicatEcho("\x01");
            else
                navicatEcho("\x00");
            pg_free_result($res);
        }
    }
    return navicatGetEchoAll();
}



function doSystemTest()
{
    $resStr="";
    function output($description, $succ, $resStr) {
        $resStr="";
        $resStr=$resStr."<tr><td class=\"TestDesc\">$description</td><td ";
        $resStr=$resStr.(($succ)? "class=\"TestSucc\">$resStr[0]</td></tr>" : "class=\"TestFail\">$resStr[1]</td></tr>");
        return $resStr;
    }
    $resStr=$resStr.output("PHP version >= 4.0.5", phpversion_int() >= 40005, array("Yes", "No"));
    $resStr=$resStr.output("pg_connect() available", function_exists("pg_connect"), array("Yes", "No"));
    if (phpversion_int() >= 40302 && substr($_SERVER["SERVER_SOFTWARE"], 0, 6) == "Apache" && function_exists("apache_get_modules")){
        if (in_array("mod_security2", apache_get_modules()))
            $resStr=$resStr.output("Mod Security 2 installed", false, array("No", "Yes"));
    }
    return $resStr;
}

header("Content-Type: text/html");

return '<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
    <title>Navicat HTTP Tunnel Tester</title>
    <meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <style type="text/css">
        body{
            margin: 30px;
            font-family: Tahoma;
            font-weight: normal;
            font-size: 14px;
            color: #222222;
        }
        table{
            width: 100%;
            border: 0px;
        }
        input{
            font-family:Tahoma,sans-serif;
            border-style:solid;
            border-color:#666666;
            border-width:1px;
        }
        fieldset{
            border-style:solid;
            border-color:#666666;
            border-width:1px;
        }
        .Title1{
            font-size: 30px;
            color: #003366;
        }
        .Title2{
            font-size: 10px;
            color: #999966;
        }
        .TestDesc{
            width:70%
        }
        .TestSucc{
            color: #00BB00;
        }
        .TestFail{
            color: #DD0000;
        }
        .mysql{
        }
        .pgsql{
        }
        .sqlite{
            display:none;
        }
        #page{
            max-width: 42em;
            min-width: 36em;
            border-width: 0px;
            margin: auto auto;
        }
        #host, #dbfile{
            width: 300px;
        }
        #port{
            width: 75px;
        }
        #login, #password, #db{
            width: 150px;
        }
        #Copyright{
            text-align: right;
            font-size: 10px;
            color: #888888;
        }
    </style>
    <script type="text/javascript">
        function getInternetExplorerVersion(){
            var ver = -1;
            if (navigator.appName == "Microsoft Internet Explorer"){
                var regex = new RegExp("MSIE ([0-9]{1,}[\.0-9]{0,})");
                if (regex.exec(navigator.userAgent))
                    ver = parseFloat(RegExp.$1);
            }
            return ver;
        }
        function setText(element, text, succ){
            element.className = (succ)?"TestSucc":"TestFail";
            element.innerHTML = text;
        }
        function getByteAt(str, offset){
            return str.charCodeAt(offset) & 0xff;
        }
        function getIntAt(binStr, offset){
            return (getByteAt(binStr, offset) << 24)+
                (getByteAt(binStr, offset+1) << 16)+
                (getByteAt(binStr, offset+2) << 8)+
                (getByteAt(binStr, offset+3) >>> 0);
        }
        function getBlockStr(binStr, offset){
            if (getByteAt(binStr, offset) < 254)
                return binStr.substring(offset+1, offset+1+binStr.charCodeAt(offset));
            else
                return binStr.substring(offset+5, offset+5+getIntAt(binStr, offset+1));
        }
        function doServerTest(){
            var version = getInternetExplorerVersion();
            if (version==-1 || version>=9.0){
                var xmlhttp = (window.XMLHttpRequest)? new XMLHttpRequest() : xmlhttp=new ActiveXObject("Microsoft.XMLHTTP");

                xmlhttp.onreadystatechange=function(){
                    var outputDiv = document.getElementById("ServerTest");
                    if (xmlhttp.readyState == 4){
                        if (xmlhttp.status == 200){
                            var errno = getIntAt(xmlhttp.responseText, 6);
                            if (errno == 0)
                                setText(outputDiv, "Connection Success!", true);
                            else
                                setText(outputDiv, parseInt(errno)+" - "+getBlockStr(xmlhttp.responseText, 16), false);
                        }else
                            setText(outputDiv, "HTTP Error - "+xmlhttp.status, false);
                    }
                }

                var params = "";
                var form = document.getElementById("TestServerForm");
                for (var i=0; i<form.elements.length; i++){
                    if (i>0) params += "&";
                    params += form.elements[i].id+"="+form.elements[i].value.replace("&", "%26");
                }

                document.getElementById("ServerTest").className = "";
                document.getElementById("ServerTest").innerHTML = "Connecting...";
                xmlhttp.open("POST", "", true);
                xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
                xmlhttp.setRequestHeader("Content-length", params.length);
                xmlhttp.setRequestHeader("Connection", "close");
                xmlhttp.send(params);
            }else{
                document.getElementById("ServerTest").className = "";
                document.getElementById("ServerTest").innerHTML = "Internet Explorer "+version+" is not supported, please use Internet explorer 9.0 or above, firefox, chrome or safari";
            }
        }
    </script>
</head>

<body>
<div id="page">
    <p>
        <font class="Title1">Navicat&trade;</font><br>
        <font class="Title2">The gateway to your database!</font>
    </p>
    <fieldset>
        <legend>System Environment Test</legend>
        <table>
            <tr style="display:none"><td width=70%>PHP installed properly</td><td class="TestFail">No</td></tr>
            '.doSystemTest().'
        </table>
    </fieldset>
    <br>
    <fieldset>
        <legend>Server Test</legend>
        <form id="TestServerForm" action="#" onSubmit="return false;">
            <input type=hidden id="actn" value="C">
            <table>
                <tr class="mysql"><td width="35%">Hostname/IP Address:</td><td><input type=text id="host" placeholder="localhost"></td></tr>
                <tr class="mysql"><td>Port:</td><td><input type=text id="port" placeholder="5432"></td></tr>
                <tr class="pgsql"><td>Initial Database:</td><td><input type=text id="db" placeholder="template1"></td></tr>
                <tr class="mysql"><td>Username:</td><td><input type=text id="login" placeholder="postgres"></td></tr>
                <tr class="mysql"><td>Password:</td><td><input type=password id="password" placeholder=""></td></tr>
                <tr class="sqlite"><td>Database File:</td><td><input type=text id="dbfile" placeholder="sqlite.db"></td></tr>
                <tr><td></td><td><br><input id="TestButton" type="submit" value="Test Connection" onClick="doServerTest()"></td></tr>
            </table>
        </form>
        <div id="ServerTest"><br></div>
    </fieldset>
    <p id="Copyright">Copyright &copy; PremiumSoft &trade; CyberTech Ltd. All Rights Reserved.</p>
</div>
</body>
</html>';