const COMPRESSED_FILE_PTN = /zip|tar|tgz|gz|rar/;
const TEXT_FILE_PTN = /txt/;
const CODE_FILE_PTN = /xml|html|java|py|c|css|js|go/;
const PDF_FILE_PTN = /pdf/;


Vue.component("file-entry", {
    props: {
        attributes: Object
    },
    computed: {
        icon() {
            let type = this.attributes.type;
            if (type === 'd') return "fa-folder";
            let suffix = this.attributes.name.split(".").pop().toLowerCase();

            if (COMPRESSED_FILE_PTN.test(suffix)) return 'fa-file-archive';
            else if (TEXT_FILE_PTN.test(suffix)) return 'fa-file-text';
            else if (CODE_FILE_PTN.test(suffix)) return 'fa-code';
            else if (PDF_FILE_PTN.test(suffix)) return 'fa-file-pdf';
            else return 'fa-file';
        },
        size() {
            return this.attributes.type === 'd' ? '-' : this.attributes["sizeForHuman"];
        },
        time() {
            if (this.attributes["modified"]) {
                let date = new Date(this.attributes["modified"]);
                return `${date.getFullYear()}/${date.getMonth()}/${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
            } else {
                return '-';
            }
        }
    },
    template: `
    <li class="file-entry">
        <i :class="['fas', icon]"></i>
        <span class="file-name" @click="clickName(attributes.name, attributes.type)">{{ attributes.name }}</span>
        <span class="" @click="deleteFile(attributes.name, attributes.type)">delete</span>
        <span>{{ size }}</span>
        <span>{{ time }}</span>
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
        deleteFile(name, type) {
            console.log("entry delete", name, type);
            if (type === 'd') {
                console.info("cannot delete directory");
            }else if (type === '-') {
                this.$emit("delete-file", name);
            }

        }
    }
});


Vue.component("file-list", {
    props: {
        entries: Array,
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
        @enter-directory="enterDirectory" @download-file="downloadFile" @delete-file="deleteFile">
        
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
        deleteFile(name){
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