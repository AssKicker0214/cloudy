Vue.component('clipboard-record', {
    props: {
        text: String,
        id: Number
    },
    template:
    `
    <div class="clipboard-record">
        <pre> {{ text }} </pre>
    </div>
    `
});


Vue.component('clipboard', {
    props: {

    },
    data: function () {
        return {
            list: [],
            newRecord: ""
        }
    },
    template:
    `
    <section class="clipboard">
    <textarea @keyup.ctrl.enter="commit()" v-model="newRecord"></textarea>
    <ul>
        <li v-for="record in list">
            <clipboard-record :text="record.text" :id="record.id"></clipboard-record>
        </li>
    </ul>
    </section>
    `,
    methods: {
        commit(){
            console.log("commit clipboard: " + this.newRecord);
            fetch("/clipboards", {
                method: 'POST',
                body: this.newRecord
            }).then(res => {
                if(res.ok)  this.newRecord = "";
            })
        },
        refresh(){
            fetch("/clipboards").then(res => {
                if(res.ok) return res.json();
            }).then(list => {
                this.list = list;
            })
        }
    },
    created(){
        this.refresh();
    }
});