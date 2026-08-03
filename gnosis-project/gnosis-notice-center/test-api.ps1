$base = "http://localhost:8091/notice"
$script:results = @()

function TestApi($name, $url, $body) {
    try {
        $r = Invoke-WebRequest -Uri $url -Method Post -ContentType "application/json" -Body $body -UseBasicParsing
        $code = $r.StatusCode
        $json = $r.Content | ConvertFrom-Json
        $apiCode = $json.code
        $msg = $json.message
        $script:results += "$name`tHTTP:$code API:$apiCode MSG:$msg"
    } catch {
        $errMsg = $_.Exception.Message
        if ($_.Exception.Response) {
            try {
                $sr = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
                $errBody = $sr.ReadToEnd()
                $script:results += "$name`tERR`t$errBody"
            } catch {
                $script:results += "$name`tERR`t$errMsg"
            }
        } else {
            $script:results += "$name`tCONN`t$errMsg"
        }
    }
}

# 1. Template
TestApi "template/page" "$base/template/page" '{"pageNum":0,"pageSize":10}'
TestApi "template/create" "$base/template/create" '{"templateCode":"TEST_API","templateName":"API","templateType":"TEXT","noticeType":"SMS","content":"test ${code}","status":1}'
$tid = ""
try {
    $r = Invoke-WebRequest -Uri "$base/template/page" -Method Post -ContentType "application/json" -Body '{"pageNum":0,"pageSize":10}' -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $tid = $j.data.list[0].id
} catch {}
if ($tid) {
    TestApi "template/detail" "$base/template/detail" "{`"id`":`"$tid`"}"
    TestApi "template/update" "$base/template/update" "{`"id`":`"$tid`",`"templateCode`":`"TEST_API`",`"templateName`":`"API2`",`"templateType`":`"TEXT`",`"noticeType`":`"SMS`",`"content`":`"mod`",`"status`":1}"
    TestApi "template/batchEnable" "$base/template/batchEnable" "[`"$tid`"]"
    TestApi "template/batchDisable" "$base/template/batchDisable" "[`"$tid`"]"
}

# 2. Send
TestApi "send/send" "$base/send/send" '{"templateCode":"TEST_API","noticeType":"SMS","receiver":"13800138000","params":{"code":"123"}}'
TestApi "send/sms" "$base/send/sms" '{"receiver":"13800138000","content":"test"}'
TestApi "send/email" "$base/send/email" '{"receiver":"t@t.com","subject":"s","content":"c"}'
TestApi "send/inbox" "$base/send/inbox" '{"receivers":["user1"],"subject":"s","content":"c"}'
TestApi "send/wechat" "$base/send/wechat" '{"receiver":"openid","content":"c"}'
TestApi "send/batch" "$base/send/batch" '{"templateCode":"TEST_API","noticeType":"SMS","receivers":["13800138000"],"params":{"code":"1"}}'

# 3. Log
TestApi "log/page" "$base/log/page" '{"pageNum":0,"pageSize":10}'
$lid = ""
try {
    $r = Invoke-WebRequest -Uri "$base/log/page" -Method Post -ContentType "application/json" -Body '{"pageNum":0,"pageSize":10}' -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $lid = $j.data.list[0].id
} catch {}
if ($lid) {
    TestApi "log/detail" "$base/log/detail" "{`"id`":`"$lid`"}"
    TestApi "log/retry" "$base/log/retry" "{`"id`":`"$lid`"}"
}

# 4. Inbox
TestApi "inbox/page" "$base/inbox/page" '{"pageNum":0,"pageSize":10}'
TestApi "inbox/create" "$base/inbox/create" '{"userId":"user1","subject":"test","content":"c","isRead":0}'
TestApi "inbox/countUnread" "$base/inbox/countUnread" '{"userId":"user1"}'
$iid = ""
try {
    $r = Invoke-WebRequest -Uri "$base/inbox/page" -Method Post -ContentType "application/json" -Body '{"pageNum":0,"pageSize":10}' -UseBasicParsing
    $j = $r.Content | ConvertFrom-Json
    $iid = $j.data.list[0].id
} catch {}
if ($iid) {
    TestApi "inbox/detail" "$base/inbox/detail" "{`"id`":`"$iid`"}"
    TestApi "inbox/markRead" "$base/inbox/markRead" "{`"id`":`"$iid`"}"
    TestApi "inbox/batchRead" "$base/inbox/batchRead" "[`"$iid`"]"
    TestApi "inbox/batchEnable" "$base/inbox/batchEnable" "[`"$iid`"]"
    TestApi "inbox/batchDisable" "$base/inbox/batchDisable" "[`"$iid`"]"
}

# 5. Group
TestApi "group/page" "$base/group/page" '{"pageNum":0,"pageSize":10}'
TestApi "group/create" "$base/group/create" '{"groupCode":"GRP1","groupName":"g1","status":1}'

# 6. Blacklist
TestApi "blacklist/page" "$base/blacklist/page" '{"pageNum":0,"pageSize":10}'
TestApi "blacklist/create" "$base/blacklist/create" '{"userId":"u1","noticeType":"SMS","reason":"r","status":1}'

# 7. ScheduledSend
TestApi "scheduledSend/page" "$base/scheduledSend/page" '{"pageNum":0,"pageSize":10}'
TestApi "scheduledSend/create" "$base/scheduledSend/create" '{"templateCode":"TEST_API","noticeType":"SMS","receiver":"13800138000","scheduledTime":"2026-07-10 10:00:00"}'

# 8. TemplateVersion
TestApi "templateVersion/page" "$base/templateVersion/page" '{"pageNum":0,"pageSize":10}'

# Output
Write-Host ""
Write-Host "========== API TEST RESULTS =========="
foreach ($r in $script:results) { Write-Host $r }
Write-Host "======================================"
