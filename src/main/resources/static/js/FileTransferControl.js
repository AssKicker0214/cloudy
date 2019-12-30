class FileTransferControl {
    constructor(file) {
        this.file = file;
    }

    upload(postURL) {
        let body = "";
        if (this.file.size < 2 ^ 20) {
            let formData = new FormData();
            formData.append("file", this.file, this.file.name);
            body = formData
        }
        return fetch(postURL, {
            method: 'POST',
            headers: {
                "Connection": "Keep-Alive"
            },
            body: body
        });
    }
}