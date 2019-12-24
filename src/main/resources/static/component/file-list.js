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
            let date = new Date(this.attributes.modified);
            console.log(date);
            return `${date.getFullYear()}/${date.getMonth()}/${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
        }
    },
    template: `
    <li class="file-entry">
        <i :class="['fas', icon]"></i>
        <span class="file-name">{{ attributes.name }}</span>
        <span>{{ size }}</span>
        <span>{{ time }}</span>
    </li>
    `
});


Vue.component("file-list", {
    props: {
        entries: Array
    },

    template:
        `
    <ul class="file-list">
        <file-entry v-for="(entry, i) in entries" :key="'entry-'+i" :attributes="entry"></file-entry>
    </ul>
    `,
    methods: {}
});