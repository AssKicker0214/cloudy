class FileTransferControl{
    constructor(file) {
        this.file = file;
    }

    upload(postURL) {
        let formData = new FormData();
        formData.append("file", this.file, this.file.name);
        return fetch(postURL, {
            method: 'POST',
            headers:{
                "Connection": "Keep-Alive"
            },
            body: formData
        });
    }
}