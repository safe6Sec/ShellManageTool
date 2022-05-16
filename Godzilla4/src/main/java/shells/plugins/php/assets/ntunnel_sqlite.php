$allowTestMenu = true;

header("Content-Type: text/plain; charset=x-user-defined");
error_reporting(0);
set_time_limit(0);

define(FileDoesNotExist, 0);
define(FileCannotBeOpened, 1);
define(FileIsSQLite2, 2);
define(FileIsSQLite3, 3);
define(FileIsInvalid, 4);


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
    @set_magic_quotes_runtime(0);
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

function EchoConnInfo()
{
    $version = sqlite_libversion();
    $str = GetBlock($version);
    $str .= GetBlock($version);
    $str .= GetBlock($version);
    navicatEcho($str);
}

function EchoConnInfo3()
{
    $version = SQLite3::version();
    $str = GetBlock($version["versionString"]);
    $str .= GetBlock($version["versionString"]);
    $str .= GetBlock($version["versionString"]);
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
    for ( $i = 0; $i < $numfields; $i++ ) {
        $str .= GetBlock(sqlite_field_name($res, $i));
        $str .= GetBlock("");

        $type = -2;	// SQLITE_TEXT
        $length = 0;
        $flag = 0;

        $str .= GetLongBinary($type);
        $str .= GetLongBinary($flag);
        $str .= GetLongBinary($length);
    }
    navicatEcho($str);
}

function EchoFieldsHeader3($res, $numfields)
{
    $str = "";
    for ( $i = 0; $i < $numfields; $i++ ) {
        $str .= GetBlock($res->columnName($i));
        $str .= GetBlock("");

        $type = SQLITE3_NULL;
        $length = 0;
        $flag = 0;

        $str .= GetLongBinary($type);
        $str .= GetLongBinary($flag);
        $str .= GetLongBinary($length);
    }
    navicatEcho($str);
}

function EchoData($res, $numfields, $numrows)
{
    for ( $i = 0; $i < $numrows; $i++ ) {
        $str = "";
        $row = sqlite_fetch_array( $res, SQLITE_NUM );
        for( $j = 0; $j < $numfields; $j++ ) {
            if( is_null($row[$j]) )
                $str .= "\xFF";
            else
                $str .= GetBlock($row[$j]);
            $str .= GetLongBinary(-2);
        }
        navicatEcho($str);
    }
}

function EchoData3($res, $numfields, $numrows)
{
    while ($row = $res->fetchArray(SQLITE3_NUM)) {
        $str = "";
        for ( $j = 0; $j < $numfields; $j++ ) {
            if ( is_null($row[$j]) )
                $str .= "\xFF";
            else
                $str .= GetBlock($row[$j]);
            $str .= GetLongBinary($res->columnType($j));
        }
        navicatEcho($str);
    }
}

function SQLite2($action, $path, $queries)
{
    if (!function_exists("sqlite_open")) {
        EchoHeader(203);
        navicatEcho(GetBlock("SQLite2 is not supported on the server"));
        return navicatGetEchoAll();
    }

    $errno_c = 0;
    $conn = sqlite_open($path, 0666, $error_c);
    if ($conn == FALSE) {
        $errno_c = 202;
    }

    EchoHeader($errno_c);
    if ($errno_c > 0) {
        navicatEcho(GetBlock(sqlite_error_string($error_c)));
    } elseif($action == "C") {
        EchoConnInfo();
    } elseif($action == "2") {
        sqlite_query($conn, "VACUUM");
        EchoConnInfo();
    } elseif($action == "Q") {
        for($i=0;$i<count($queries);$i++) {
            $query = $queries[$i];
            if ($query == "") continue;
            if (phpversion_int() < 50400){
                if(get_magic_quotes_gpc())
                    $query = stripslashes($query);
            }
            $res = sqlite_query($conn, $query);
            $errno = sqlite_last_error($conn);
            $affectedrows = sqlite_changes($conn);
            $insertid = sqlite_last_insert_rowid($conn);
            $numfields = sqlite_num_fields($res);
            $numrows = sqlite_num_rows($res);
            EchoResultSetHeader($errno, $affectedrows, $insertid, $numfields, $numrows);
            if ($errno != 0)
                navicatEcho(GetBlock(sqlite_error_string($errno)));
            else {
                if ($numfields > 0) {
                    EchoFieldsHeader($res, $numfields);
                    EchoData($res, $numfields, $numrows);
                } else {
                    navicatEcho(GetBlock(""));
                }
            }
            if ($i<(count($queries)-1))
                navicatEcho("\x01");
            else
                navicatEcho("\x00");
        }
    }

    sqlite_close($conn);
}

function SQLite3($action, $path, $queries)
{
    if (!class_exists("Sqlite3")) {
        EchoHeader(203);
        navicatEcho(GetBlock("SQLite3 is not supported on the server"));
        return navicatGetEchoAll();
    }

    $flag = SQLITE3_OPEN_READWRITE;
    if ($action == "3")
        $flag = SQLITE3_OPEN_READWRITE | SQLITE3_OPEN_CREATE;

    $errno_c = 0;
    $conn = new SQLite3($path, $flag);
    if ($conn == nil) {
        $errno_c = 202;
    }

    EchoHeader($errno_c);
    if ($errno_c > 0) {
        navicatEcho(GetBlock(SQLite3::lastErrorMsg()));
    } else if($action == "C") {
        EchoConnInfo3();
    } else if($action == "3") {
        $conn->query("VACUUM");
        EchoConnInfo3();
    } else if($action == "Q") {
        for($i=0;$i<count($queries);$i++) {
            $query = $queries[$i];
            if ($query == "") continue;
            if (phpversion_int() < 50400){
                if(get_magic_quotes_gpc())
                    $query = stripslashes($query);
            }
            $res = $conn->query($query);
            $errno = $conn->lastErrorCode();
            $affectedrows = $conn->changes();
            $insertid = $conn->lastInsertRowID();
            $numfields = 0;
            $numrows = 0;
            if (is_a($res, "SQLite3Result")) {
                $numfields = $res->numColumns();
                if ($numfields > 0) {
                    while ($row = $res->fetchArray(SQLITE3_NUM)) {
                        $numrows++;
                    }
                    $res->reset();
                }
            }
            EchoResultSetHeader($errno, $affectedrows, $insertid, $numfields, $numrows);
            if ($errno != 0)
                navicatEcho(GetBlock($conn->lastErrorMsg()));
            else {
                if ($numfields > 0) {
                    EchoFieldsHeader3($res, $numfields);
                    EchoData3($res, $numfields, $numrows);
                    $res->finalize();
                } else {
                    navicatEcho(GetBlock(""));
                }
            }
            if ($i<(count($queries)-1))
                navicatEcho("\x01");
            else
                navicatEcho("\x00");
        }
    }

    $conn->close();
}

$_POST["q[]"]=array($_POST["q[]"]);

if (phpversion_int() < 50000) {
    EchoHeader(201);
    navicatEcho(GetBlock("unsupported php version"));
    return navicatGetEchoAll();
}

if (!isset($_POST["actn"]) || !isset($_POST["dbfile"])) {
    $testMenu = $allowTestMenu;
    if (!$testMenu){
        EchoHeader(202);
        navicatEcho(GetBlock("invalid parameters"));
        return navicatGetEchoAll();
    }
}

if (!$testMenu){
    if ($action == "2" || $action == "3") {
        if (!isset($_POST["version"])) {
            EchoHeader(202);
            navicatEcho(GetBlock("invalid parameters"));
            return navicatGetEchoAll();
        }
    }

    if ($_POST["encodeBase64"] == '1') {
        for($i=0;$i<count($_POST["q[]"]);$i++)
            $_POST["q[]"][$i] = base64_decode($_POST["q[]"][$i]);
    }

    $action = $_POST["actn"];
    $file = $_POST["dbfile"];
    $queries = $_POST["q[]"];

    $status = FileDoesNotExist;
    if (is_file($file)) {
        $fhandle = fopen($file, "r");
        if ($fhandle) {
            $sqlite2header = "** This file contains an SQLite 2.1 database **";
            $sqlite3header = "SQLite format 3";
            $string = fread($fhandle, strlen($sqlite2header));
            if (strncmp($string, $sqlite2header, strlen($sqlite2header)) == 0)
                $status = FileIsSQLite2;
            else if ($string == "" || strncmp(substr($string, 0, strlen($sqlite3header)), $sqlite3header, strlen($sqlite3header)) == 0)
                $status = FileIsSQLite3;
            else
                $status = FileIsInvalid;
            fclose($fhandle);
        } else {
            $status = FileCannotBeOpened;
        }
    }

    if ($action == "2" || $action == "3") {
        if ($status == FileDoesNotExist) {
            if ($action == "2")
                SQLite2($action, $file, $queries);
            else
                SQLite3($action, $file, $queries);
        } else {
            EchoHeader(202);
            navicatEcho(GetBlock("Datbase file exists already"));
        }
    } else {
        switch ($status) {
            case FileDoesNotExist:
                EchoHeader(202);
                navicatEcho(GetBlock("Database file does not exist"));
                break;
            case FileCannotBeOpened:
                EchoHeader(202);
                navicatEcho(GetBlock("Database file cannot be opened"));
                break;
            case FileIsSQLite2:
                SQLite2($action, $file, $queries);
                break;
            case FileIsSQLite3:
                SQLite3($action, $file, $queries);
                break;
            case FileIsInvalid:
                EchoHeader(202);
                navicatEcho(GetBlock("Database file is encrypted or invalid"));
                break;
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
    $resStr=$resStr.output("[SQLite2] PHP version >= 5.0.0", phpversion_int() >= 50000, array("Yes", "No"));
    $resStr=$resStr.output("[SQLite2] sqlite_open() available", function_exists("sqlite_open"), array("Yes", "No"));
    $resStr=$resStr.output("[SQLite3] PHP version >= 5.3.0", phpversion_int() >= 50300, array("Yes", "No"));
    $resStr=$resStr.output("[SQLite3] SQLite3 class available", class_exists("SQLite3"), array("Yes", "No"));
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
			display:none;
		}
		.pgsql{
			display:none;
		}
		.sqlite{
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
		<tr class="mysql"><td>Port:</td><td><input type=text id="port" placeholder=""></td></tr>
		<tr class="pgsql"><td>Initial Database:</td><td><input type=text id="db" placeholder="template1"></td></tr>
		<tr class="mysql"><td>Username:</td><td><input type=text id="login" placeholder=""></td></tr>
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