const COMPRESSED_FILE_PTN = /zip|tar|tgz|gz|rar/;
const TEXT_FILE_PTN = /txt/;
const CODE_FILE_PTN = /xml|html|java|py|c|cpp|sh|bat|md|css|js|go/;
const PDF_FILE_PTN = /pdf/;
const IMAGE_FILE_PTN = /png|jpg|jpeg|svg|gif|bmp|ico|tif/;
const PPT_FILE_PTN = /ppt|pptx/;
const EXCEL_FILE_PTN = /xls|xlsx/;
const WORD_FILE_PTN = /doc|docx/;
const VIDEO_FILE_PTN = /mp4|avi|mov|mpe?g|wmv|mkv/;
const AUDIO_FILE_PTN = /mp3|wav|ogg/;


Vue.component("file-entry", {
    props: {
        attributes: Object,
        directoryPath: String
    },
    computed: {
        icon() {
            let type = this.attributes.type;
            if (type === 'd') return "fa fa-folder";
            let suffix = this.attributes.name.split(".").pop().toLowerCase();

            if (COMPRESSED_FILE_PTN.test(suffix)) return 'fa fa-file-archive';
            else if (TEXT_FILE_PTN.test(suffix)) return 'fa fa-file';
            else if (CODE_FILE_PTN.test(suffix)) return 'fa fa-code';
            else if (PDF_FILE_PTN.test(suffix)) return 'fa fa-file-pdf';
            else if (IMAGE_FILE_PTN.test(suffix)) return 'fa fa-file-image';
            else if (PPT_FILE_PTN.test(suffix)) return 'fa fa-file-powerpoint';
            else if (EXCEL_FILE_PTN.test(suffix)) return 'fa fa-file-excel';
            else if (WORD_FILE_PTN.test(suffix)) return 'fa fa-file-word';
            else if(VIDEO_FILE_PTN.test(suffix)) return 'fa fa-file-video';
            else if(AUDIO_FILE_PTN.test(suffix)) return 'fa fa-file-audio';
            else return 'fa fa-file';
        },
        size() {
            return this.attributes.type === 'd' ? '-' : this.attributes["sizeForHuman"];
        },
        time() {
            if (this.attributes["modified"]) {
                let date = new Date(this.attributes["modified"]);
                let yyyy = date.getFullYear();
                let MM = date.getMonth() + 1;
                let dd = date.getDate();
                let HH = utils.pad(date.getHours(), 2, 0);
                let mm = utils.pad(date.getMinutes(), 2, 0);
                return `${yyyy}/${MM+1}/${dd} ${HH}:${mm}`
            } else {
                return '-';
            }
        },
        previewAddressPrefix(){
            return "http://"+window.location.hostname+":8080"+this.directoryPath
        }
    },
    template: `
    <li class="file-entry">
        <a :href="previewAddressPrefix+attributes.name" target="_blank" :class="['icon', icon]"></a>
        <span class="file-name" @click="clickName(attributes.name, attributes.type)">{{ attributes.name }}</span>
        <span class="file-time">{{ time }}</span>
        <span class="file-size">{{ size }}</span>
        <i class="remove fa fa-trash-alt" @click="remove(attributes.name, attributes.type)"></i>
    </li>
    `,
    methods: {
        clickName(name, type) {
            if (type === 'd') {
                this.$emit("enter-directory", name);
            } else if (type === '-') {
                this.$emit("download-file", name);
            }
        },
        remove(name, type) {
            console.log("entry delete", name, type);
            if (type === 'd') {
                console.info("cannot delete directory");
            } else if (type === '-') {
                this.$emit("delete-file", name);
            }

        }
    }
});


Vue.component("file-list", {
    props: {
        entries: Array,
        directoryPath: String
    },
    data: function () {
        return {
            dropping: false
        }
    },
    computed: {
        _entries() {
            return [{type: 'd', name: '..'}].concat(this.entries);
        }
    },

    template:
        `
    <ul class="file-list" @dragover="dragover($event)" @dragleave="dragleave($event)" @drop="upload($event)">
        <file-entry v-for="(entry, i) in _entries" :key="'entry-'+(i+1)" :attributes="entry" 
        @enter-directory="enterDirectory" @download-file="downloadFile" @delete-file="deleteFile"
        :directory-path="directoryPath">
        
        </file-entry>
        <div class="mask" :style="{opacity: dropping ^ 0}">Drop to upload</div>
    </ul>
    `,
    methods: {
        enterDirectory(name) {
            this.$emit("enter-directory", name);
        },
        downloadFile(name) {
            this.$emit("download-file", name);
        },
        deleteFile(name) {
            this.$emit("delete-file", name);
        },
        dragover(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            this.dropping = true;
            return false;
        },
        dragleave(evt) {
            evt.stopPropagation();
            evt.preventDefault();
            this.dropping = false;
            // console.log("leave");
            return false;
        },
        upload(evt) {
            evt.stopPropagation();
            evt.preventDefault();

            this.dropping = false;
            let files = Array.from(evt.dataTransfer.files);
            this.$emit("upload-file", files);
            return false;
        }
    }
});