const { createApp } = Vue;

createApp({
    data() {
        return {
            account: {},
            transactions: [],
            checked: undefined
        }
    },
    created() {
        this.checked = JSON.parse(localStorage.getItem("checked"))
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get('id');
        this.loadData(id);
            
    },
    methods: {
        loadData(id) {
            axios.get(`http://localhost:8080/api/accounts/${id}`)
                .then(accountData => {
                    this.account = accountData.data;
                    this.transactions = this.account.transactions.sort((a, b) => b.id - a.id); 
                })
        },
        tableRowStyle(transactionType) {
            let style = {};
            if (transactionType === 'CREDIT') {
                style.backgroundColor = '#c1fba4';
            } else {
                style.backgroundColor = '#ff928b';
            }
            return style;
        }
        
    },
    computed: {
        saveOnLocalStorage() {
            localStorage.setItem("checked", JSON.stringify(this.checked)); 
            console.log(this.checked)
        }
    }
}).mount('#app')