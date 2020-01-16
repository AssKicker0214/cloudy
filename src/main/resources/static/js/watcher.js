class Watcher {
    _handler;
    wsTopic;
    _ws;
    constructor(wsTopic, handler) {
        this._handler = handler;
        this.wsTopic = wsTopic;
        this._ws = new WebSocket(`ws://${window.location.host}/${wsTopic}`);
        this.initWS();
    }

    initWS() {
        this._ws.onopen = () => console.info("watcher standby for: " + this.wsTopic);
        this._ws.onclose = (evt) => {
            this.reconnect();
        };
        this._ws.onmessage = evt => {
            let text = evt.data;
            let msg = JSON.parse(text);
            // noinspection JSUnresolvedVariable
            if("function" === typeof this._handler.react){
                // noinspection JSUnresolvedFunction
                this._handler.react(msg['cmd'], msg['args'])
            }
        }
    }

    reconnect() {
        let retryTime = 5;
        let retry = () => {
            console.log("reconnecting...");
            let ws = new WebSocket(`ws://${window.location.host}/${this.wsTopic}`);
            ws.onopen = () => {
                console.info("watcher reconnected for: ", this.wsTopic);
                ws.onmessage = this._ws.onmessage;
                ws.onclose = this._ws.onclose;
                this._ws = ws;
            };
            ws.onclose = () => {
                retryTime -= 1;
                if (retryTime > 0) setTimeout(retry, 5000);
            }

        };

        setTimeout(retry, 5000);
    }
}
