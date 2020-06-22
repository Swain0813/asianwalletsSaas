<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
        *{margin: 0;
            padding: 0;}
        html{
            width: 100%;
            height: 100%;
        }
        body{
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }
        .content{

            background-color: #cd5b19;

            box-sizing: border-box;
            padding: 15px 25px;
            color: #fff;
            display: flex;
            flex-direction: column;
            align-items: center;
            border-radius:15px ;
        }
        .topText{
            font-size: 36px;
            margin-bottom:25px ;
        }
        .toptitle{
            font-size: 16px;
            margin-bottom:45px ;
        }
        .imageB{
            height: 190px;
            width: 190px;
            border: 1px #fff solid;
            margin-bottom: 15px;
        }
        .topspan{
            display: inline-block;
            margin-bottom: 25px;
        }
        .list{
            width: 100%;
            border-top:1px  dashed #fff;
            padding-top:20px ;
            font-size: 16px;
        }
        .list p{
            line-height: 25px;
            display: inline-block;
            margin-right: 15px;
        }
        .list span{
            line-height: 25px;
            display: inline-block;

        }
        .list div{
            display: flex;
            flex-direction: row;

        }
        .list li{
            list-style-type: none;
            line-height: 25px;
        }
    </style>
</head>
<body>
<div class="content">
    <p class="topText">恭喜</p>
    <p class="toptitle">【${merchantName}】 ${activityTheme}${content}</p>
    <img class="imageB" src=${ticketQrCode}></img>
    <span class="topspan">${ticketId}</span>
    <div class="list">
        <div><p>【可用时间】</p><span>${startTime}---${endTime}</span></div>
        <div><p>【不可用时间】</p><span>${unusableTime}</span></div>
        <div>
            <p>【使用规则】</p>
             ${ruleDescription}
        </div>
        <div><p>【商家地址】</p><span>${shopAddresses}</span></div>
    </div>
</div>
</body>
</html>
