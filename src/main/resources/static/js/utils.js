const utils = {
    copy(text, evt) {
        if (evt) {
            evt.stopPropagation();
            evt.preventDefault();
        }

        if (window.clipboardData && window.clipboardData.setData) {
            window.clipboardData.setData("Text", text);
        }else if (document.queryCommandSupported && document.queryCommandSupported("copy")) {
            const textarea = document.createElement("textarea");
            textarea.textContent = text;
            textarea.style.position = "fixed";
            textarea.style.opacity = "0";
            document.body.appendChild(textarea);
            textarea.select();
            try{
                document.execCommand("copy");
            }catch (e) {
                console.warn("copy failed", e)
            }finally {
                document.body.removeChild(textarea);
            }
        }
        return false;
    },
    timeAgo(ms) {
        const SECOND = 1000;
        const MINUTE = 60 * SECOND;
        const HOUR = 60 * MINUTE;
        const DAY = 24 * HOUR;
        const MONTH = 30 * DAY;
        const YEAR = 12 * MONTH;


    }
};

/*
class DownCounter{
    constructor(totalMs, stepMs, cb) {
        this.totalMs = totalMs;
        this.stepMs = stepMs;
        this.cb = cb;
    }

    go(){
        let counter = (leftMs) => {
            if (leftMs > 0) {
                window.setTimeout(counter, this.stepMs, leftMs-this.stepMs)
            }else{
                return new Promise()
            }
        }
    }
}*/
