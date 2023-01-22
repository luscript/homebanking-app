const { createApp } = Vue;

createApp({
    data() {
        return {
            client: {},
            accounts: [],
            checked: undefined,
            transactionAccount: 'local',
            originAccount: undefined,
            destinyAccount: undefined,
            amount: 0,
            description: ''
        }
    },
    created() {
        this.checked = JSON.parse(localStorage.getItem("checked"))
        this.loadData();
    },
    methods: {
        loadData() {
            axios.get('http://localhost:8080/api/clients/current')
                .then(data => {
                    this.client = data.data;
                    this.accounts = data.data.accounts;
                    console.log(this.client)
                })
                .catch(err => console.log(err))
        },
        click() {
            axios.post('/api/logout').then(response => window.location.href = "http://localhost:8080/web/index.html")
        },
        createAccount() {
            axios.post()
                .then(data => {
                    console.log(data);
                    this.loadData();
                })
                .catch(err => console.log(err))
        },
        filterAccounts() {
            return this.accounts.filter(account => account.number != this.originAccount)
        },
        async makeTransaction() {
            console.log(this.originAccount)
            console.log(this.destinyAccount)
            console.log(this.amount)
            console.log(this.description)
            let account = this.accounts.find(account => account.number == this.originAccount)
            console.log(account)
            if (!this.originAccount || !this.destinyAccount || !this.description) {
                swal.fire('Please fill in all the fields', ':C', 'error')
            } else if (account.balance < this.amount) {
                console.log('hiola')
                swal.fire('Not enough funds', 'try sending less money :D', 'error')
            } else if (this.amount <= 0) {
                swal.fire('Please choose a valid amount', 'cmon... why would you send less than $1?', 'error')
            } else {
                const { value: question } = await swal.fire({
                    icon: 'question',
                    title: 'Are you sure?',
                    text: `You are about to transfer $${this.amount}`,
                    showCancelButton: true
                })
                console.log(question)
                if (question) {
                    console.log('listo')
                    axios.post('http://localhost:8080/api/clients/current/transactions', `amount=${this.amount}&description=${this.description}&originNumber=${this.originAccount}&destinyNumber=${this.destinyAccount}`)
                        .then(response => {
                            console.log(response);
                            swal.fire('Done!', 'your transaction has been sent', 'success')

                        })
                        .catch(err => swal.fire('Account not found', 'try sending money to an existing account...', 'error'))
                }
            }

        }
    },
    computed: {
        saveOnLocalStorage() {
            localStorage.setItem("checked", JSON.stringify(this.checked));
            console.log(this.checked)
        }
    }

}).mount('#app')