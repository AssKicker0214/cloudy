Vue.component('clipboard-record', {
    props: {
        text: String,
        id: Number
    },
    template:
    `
    <li class="clipboard-record">
        <button class="fas fa-copy" @click="copy()"></button>
        <textarea class="thin-scrollbar" v-model="text" @focus="selectAll($event)" readonly/>
    </li>
    `,
    methods: {
        selectAll(evt){
            let textarea = evt.target;
            textarea.select();
        },
        copy(){
            if (window.clipboardData && window.clipboardData.setData) {
                window.clipboardData.setData("Text", this.text);
            }
        }
    }
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
        <clipboard-record v-for="record in list" :text="record.text" :id="record.id">
        </clipboard-record>
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

