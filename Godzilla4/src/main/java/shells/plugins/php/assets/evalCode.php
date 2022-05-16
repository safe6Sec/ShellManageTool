ob_start();@eval(get("plugin_eval_code"));$result=ob_get_contents(); ob_end_clean();return $result;
