# ts3bot
Basic bot for ts3

mysql:
```
CREATE TABLE `privates` (
  `cid` int(11) NOT NULL,
  `name` varchar(128) COLLATE utf8_polish_ci NOT NULL,
  `prefix` varchar(13) COLLATE utf8_polish_ci NOT NULL,
  `sub` int(11) NOT NULL DEFAULT '1'
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_polish_ci;
INSERT INTO `privates` (`cid`, `name`, `prefix`, `sub`) VALUES
(100, '1. Channel', '1.', 0),
(101, '╔ sub', '╔', 1),
(102, '╠ ', '╠', 1),
(103, '╚ subix', '╚', 1),
```

functions : 
- check channels from privates for expired (empty seconds time)
- check new name channel on edit
- change name of channel user online when join/leave
- create channel with schemat when write in private to bot !newChannel
```
1. Channel name
  ╔ sub1
  ╠ sub2
  ╚ sub3
```
