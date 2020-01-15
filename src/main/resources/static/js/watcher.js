const watcher = {
    ws: new WebSocket(`ws://${window.location.host}/broadcaster`),
    broadcast(text){
        this.ws.send(text);
    },
    onmessage(cb){
        this.ws.onmessage = evt => {
            let msg = evt.data;
            cb(msg);
        }
    }
};
watcher.ws.onopen = () => console.log("watcher standby");
watcher.ws.onclose = () => console.error("watcher closed");
