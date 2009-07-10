<html>
<head>
    <g:render template="/layouts/layoutHeader"></g:render>
</head>
<body class="yui-skin-sam">
    <script>
        var config = {
            id : "audioDeneme",
            soundFile:"lovetheme.mp3",
            url: "script/run/audioDeneme",
            pollingInterval : 30
        };
        var player = new YAHOO.rapidjs.component.AudioPlayer(config);
        player.poll();

        function start(){
            YAHOO.rapidjs.Components['audioDeneme'].play();
        }
        function stop(){
            YAHOO.rapidjs.Components['audioDeneme'].stop();
        }
        function pause(){
            YAHOO.rapidjs.Components['audioDeneme'].pause();
        }
        function resume(){
            YAHOO.rapidjs.Components['audioDeneme'].resume();
        }
        function mute(){
            YAHOO.rapidjs.Components['audioDeneme'].mute();
        }
        function unmute(){
            YAHOO.rapidjs.Components['audioDeneme'].unmute();
        }
    </script>
    <button onclick="start()">Start</button>
    <button onclick="stop()">Stop</button>
    <button onclick="pause()">Pause</button>
    <button onclick="resume()">Resume</button>
    <button onclick="mute()">Mute</button>
    <button onclick="unmute()">Unmute</button>
</body>
</html>