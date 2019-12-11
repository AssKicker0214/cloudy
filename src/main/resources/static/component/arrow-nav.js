Vue.component("arrow-nav", {
    props: {
        /**
         * [
         *      {
         *          text: String
         *      }
         * ]
         */
        path: Array
    },
    template:
    `
    <nav class="arrow-nav">
        <span class="arrow" v-for="step in path"> {{ step.text }}</span>
    </nav>
    `
})