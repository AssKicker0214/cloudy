const COMPRESSED_FILE_PTN = /zip|tar|tgz|gz|rar/;
const TEXT_FILE_PTN = /txt/;
const CODE_FILE_PTN = /xml|html|java|py|c|css|js|go/;
const PDF_FILE_PTN = /pdf/;


Vue.component("file-entry", {
    props: {
        attributes: Object
    },
    computed: {
        icon(){
            let type = this.attributes.type;
            if(type === 'd') return "fa-folder";
            let suffix = this.attributes.name.split(".").pop().toLowerCase();

            if(COMPRESSED_FILE_PTN.test(suffix))   return 'fa-file-archive';
            else if(TEXT_FILE_PTN.test(suffix))    return 'fa-file-text';
            else if(CODE_FILE_PTN.test(suffix))    return 'fa-file-code';
            else if(PDF_FILE_PTN.test(suffix))     return 'fa-file-pdf';
            else    return 'fa-file';
        },
        size(){
            return this.attributes.type === 'd' ? '-' : this.attributes.sizeForHuman;
        },
        time(){
            if (this.attributes.modified) {
            let date = new Date(this.attributes.modified);
            return `${date.getFullYear()}/${date.getMonth()}/${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
            }else{
                return '-';
            }
        }
    },
    template: `
    <li class="file-entry">
        <i :class="['fas', icon]"></i>
        <span class="file-name" @click="clickName(attributes.name, attributes.type)">{{ attributes.name }}</span>
        <span>{{ size }}</span>
        <span>{{ time }}</span>
    </li>
    `,
    methods: {
          clickName(name, type){
              if (type === 'd') {
                  this.$emit("enter-directory", name);
              }else if (type === '-') {
                  this.$emit("download-file", name);
              }
          }
    }
});


Vue.component("file-list", {
    props: {
        entries: Array
    },
    computed: {
        _entries(){
            return [{type: 'd', name: '..'}].concat(this.entries);
        }
    },

    template:
        `
    <ul class="file-list">
        <file-entry v-for="(entry, i) in _entries" :key="'entry-'+(i+1)" :attributes="entry" 
        @enter-directory="enterDirectory" @download-file="downloadFile">
        
        </file-entry>
    </ul>
    `,
    methods: {
        enterDirectory(name) {
            this.$emit("enter-directory", name);
        },
        downloadFile(name) {
            this.$emit("download-file", name);
        }
    }
});