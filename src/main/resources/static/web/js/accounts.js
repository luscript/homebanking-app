const { createApp } = Vue;

createApp({
    data() {
        return {
            client: {},
            accounts: [],
            checked: undefined
        }
    },
    created() {
        this.checked = JSON.parse(localStorage.getItem("checked"))
        this.loadData();
    },
    methods: {
        loadData() {
            axios.get('/api/clients/current')
                .then(data => {
                    this.client = data.data;
                    this.accounts = data.data.accounts;
                    console.log(this.accounts)
                    this.accounts = this.accounts.filter(account => account.enabled == true);
                    console.log(this.client)
                    console.log(this.accounts)
                })
                .catch(err => console.log(err))
        },
        click() {
            axios.post('/api/logout').then(response => window.location.href = "/web/index.html")
        },
        async createAccount() {
            const inputOptions = new Promise((resolve) => {
                setTimeout(() => {
                    resolve({
                        'CHECKING': 'Checking',
                        'SAVING': 'Saving'
                    })
                }, 1000)
            })

            const { value: accountType } = await Swal.fire({
                title: 'Select account type',
                input: 'radio',
                inputOptions: inputOptions,
                inputValidator: (value) => {
                    if (!value) {
                        return 'You need to choose a card type!'
                    }
                }
            })

            if (accountType) {
                const { value: question } = await swal.fire({
                    icon: 'question',
                    title: 'Are you sure?',
                    text: `You are about to create a ${accountType} account`,
                    showCancelButton: true
                })
                if (question) {
                    axios.post('/api/clients/current/accounts', `accountType=${accountType}`)
                        .then(data => {
                            console.log(data);
                            this.loadData();
                        })
                        .catch(err => console.log(err))
                }
            }
        },
        async disableAccount(account) {
            const { value: question } = await swal.fire({
                icon: 'question',
                title: 'Are you sure?',
                text: `You are about to disable ${account.number} account`,
                showCancelButton: true
            })
            if (question) {
                axios.patch(`/api/clients/current/accounts/${account.id}`)
                    .then(data => {
                        console.log(data);
                        this.loadData();
                    })
                    .catch(err => {
                        if(err.response.data == "Cannot delete accounts that have money") {
                            Swal.fire('Cannot delete accounts with balance', ':c', 'error')
                        }
                    })
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