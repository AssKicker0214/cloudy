Vue.component("file-entry", {
    props: {
        attributes: Object
    },
    template:
    `
    <li>
    {{ attributes.name }}
</li>
    `
});


Vue.component("file-list", {
    props: {
        entries: Array
    },

    template:
    `
    <ul>
        <file-entry v-for="(entry, i) in entries" :key="'entry-'+i" :attributes="entry"></file-entry>
    </ul>
    `,
    methods: {

    }
});