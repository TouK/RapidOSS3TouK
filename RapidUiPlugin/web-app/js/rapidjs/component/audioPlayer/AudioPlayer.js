YAHOO.namespace('rapidjs', 'rapidjs.component');

YAHOO.rapidjs.component.AudioPlayer = function(config) {
    YAHOO.rapidjs.component.AudioPlayer.superclass.constructor.call(this, null, config);
    this.volume = config.volume || 100;
    this.playCondition = config.playCondition;
    this.soundFile = config.soundFile;
    this.soundObject = null;

    YAHOO.util.Event.onDOMReady(function() {
        if (soundManager._didInit) {
            this.createSound();
        }
        else {
            soundManager.onLoadEvent.subscribe(this.createSound, this, true);
        }
    }, this, true)

};

YAHOO.extend(YAHOO.rapidjs.component.AudioPlayer, YAHOO.rapidjs.component.PollingComponentContainer, {
    createSound: function() {
        this.soundObject = soundManager.createSound({
            id:this.id,
            url:this.soundFile,
            volume:this.volume
        })
    },

    handleSuccess: function(response, keepExisting, removeAttribute) {
        var conditionResult = true;
        if (this.playCondition != null) {
            var xmlObject = null;
            if(response.responseXML){
                xmlObject = response.responseXML.firstChild;                
            }
            var params = {response:response, xmlObject:xmlObject}
            conditionResult = eval(this.playCondition)
        }
        if (conditionResult) {
            this.play();
        }
    },

    play:function() {
        if (this.soundObject && this.soundObject.playState == 0) {
            soundManager.play(this.id)
        }

    },
    pause:function() {
        soundManager.pause(this.id)
    },
    resume:function() {
        soundManager.resume(this.id)
    },
    mute:function() {
        soundManager.mute(this.id)
    },
    unmute:function() {
        soundManager.unmute(this.id)
    },


    stop:function() {
        soundManager.stop(this.id)
    }


})
soundManager.flashVersion = 9
soundManager.url = getUrlPrefix() + "images/rapidjs/soundmanager/"
soundManager.wmode = 'transparent'
soundManager.debugMode = false;
soundManager.onLoadEvent = new YAHOO.util.CustomEvent("onLoadEvent");
soundManager.onload = function() {
    soundManager.onLoadEvent.fireDirect();
}