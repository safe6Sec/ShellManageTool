function webShellScan($scanPath){
    $a = $scanPath;
    $a = $a == '' ? '.' : $a;
    $b = array_merge(glob("$a/*.php"), glob("$a/*.inc"));
    $c = ['(\$_(GET|POST|REQUEST)\[.{0,15}\]\s{0,10}\(\s{0,10}\$_(GET|POST|REQUEST)\[.{0,15}\]\))',
        '((eval|assert)(\s|\n)*\((\s|\n)*\$_(POST|GET|REQUEST)\[.{0,15}\]\))',
        '(eval(\s|\n)*\(base64_decode(\s|\n)*\((.|\n){1,200})',
        '(function\_exists\s*\(\s*[\'|\"](popen|exec|proc\_open|passthru)+[\'|\"]\s*\))',
        '((exec|shell\_exec|passthru)+\s*\(\s*\$\_(\w+)\[(.*)\]\s*\))', '(\$(\w+)\s*\(\s.chr\(\d+\)\))',
        '(\$(\w+)\s*\$\{(.*)\})', '(\$(\w+)\s*\(\s*\$\_(GET|POST|REQUEST|COOKIE|SERVER)+\[(.*)\]\s*\))',
        '(\$\_(GET|POST|REQUEST|COOKIE|SERVER)+\[(.*)\]\(\s*\$(.*)\))', '(\$\_\=(.*)\$\_)',
        '(\$(.*)\s*\((.*)\/e(.*)\,\s*\$\_(.*)\,(.*)\))', '(new com\s*\(\s*[\'|\"]shell(.*)[\'|\"]\s*\))',
        '(echo\s*curl\_exec\s*\(\s*\$(\w+)\s*\))',
        '((fopen|fwrite|fputs|file\_put\_contents)+\s*\((.*)\$\_(GET|POST|REQUEST|COOKIE|SERVER)+\[(.*)\](.*)\))',
        '(\(\s*\$\_FILES\[(.*)\]\[(.*)\]\s*\,\s*\$\_(GET|POST|REQUEST|FILES)+\[(.*)\]\[(.*)\]\s*\))',
        '(\$\_(\w+)(.*)(eval|assert|include|require|include\_once|require\_once)+\s*\(\s*\$(\w+)\s*\))',
        '((include|require|include\_once|require\_once)+\s*\(\s*[\'|\"](\w+)\.(jpg|gif|ico|bmp|png|txt|zip|rar|htm|css|js)+[\'|\"]\s*\))',
        '(eval\s*\(\s*\(\s*\$\$(\w+))',
        '((eval|assert|include|require|include\_once|require\_once|array\_map|array\_walk)+\s*\(\s*\$\_(GET|POST|REQUEST|COOKIE|SERVER|SESSION)+\[(.*)\]\s*\))',
        '(preg\_replace\s*\((.*)\(base64\_decode\(\$)'];
    $ret=array();
    foreach($b as $f) {
        $g = file_get_contents($f);
        $h = explode("\n", $g);
        foreach($c as $i) {
            if (preg_match_all("/$i/iU", $g, $j)) {
                foreach($h as $k => $n) {
                    if (preg_match_all("/$i/iU", $n, $j)) {
                        array_push($ret,base64_encode($f)."\t".base64_encode($k)."\t".base64_encode($j[0][0]));
                    }
                }
            }
        }
    }
    return implode("\n",$ret);
}
return webShellScan(get("scanPath"));