class WebSocketWrapper {
    /**
     *
     * @param topic e.g. `clipboard`
     * @param autoReconnect reconnect on closing
     */
    constructor(topic, autoReconnect) {
        this.uri = `ws://${window.location.host}/${topic}`;
        this.core = new WebSocket(this.uri);
        this.autoReconnect = !!autoReconnect;
        this.__onmessageHandler = null;
        this.__onopenHandler = null;
        this.__onerrorHandler = null;
        this.__oncloseHandler = null;
    }

    send(text){
        this.core.send(text);
    }

    onmessage(fn) {
        this.__onmessageHandler = fn;
        this.core.onmessage = (evt) => {
            fn(evt);
        };
        return this;
    }

    onerror(fn) {
        this.__onerrorHandler = fn;
        this.core.onerror = (evt) => {
            console.error(evt);
            fn(evt);
        }
    }

    onopen(fn) {
        this.__onopenHandler = fn;
        this.core.onopen = (evt) => {
            console.log("WS opened");
            fn(evt);
        }
    }

    onclose(fn) {
        this.__oncloseHandler = fn;
        this.core.onclose = (evt) => {
            console.log("WS closing");
            fn(evt);
            let retry = () => {
                if (this.autoReconnect && [WebSocket.CLOSED, WebSocket.CLOSING].includes(this.core.readyState)) {
                    console.log("reconnecting");
                    this.core = new WebSocket(this.uri);
                    this.core.onopen = this.__onopenHandler;
                    this.core.onclose = this.__oncloseHandler;
                    this.core.onmessage = this.__onmessageHandler;
                    this.core.onerror = this.__onerrorHandler;
                    setTimeout(retry, 5000);
                }
            };
            retry();
        }
    }
}