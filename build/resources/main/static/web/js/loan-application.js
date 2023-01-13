const { createApp } = Vue;

createApp({
    data() {
        return {
            client: {},
            accounts: [],
            checked: undefined,
            destinyAccount: undefined,
            amount: 0,
            loans: [],
            chosenLoanName: undefined,
            chosenLoan: undefined,
            payments: undefined
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
                    axios.get('http://localhost:8080/api/loans')
                        .then(data => {
                            this.loans = data.data;
                            console.log(this.loans);
                        })
                        .catch(err => console.log(err))
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
        async requestLoan() {
            if(this.client.clientLoans.find(loan => loan.name == this.chosenLoanName)) {
                swal.fire("You've already requested this loan", ':C', 'error')
            } else if(!this.destinyAccount || !this.chosenLoan || !this.payments) {
                swal.fire('Please fill in all the fields', ':C', 'error')
            } else if(this.chosenLoan.maxAmount < this.amount){
                console.log('hiola')
                swal.fire("You exceeded the loan's max amount", 'choose an amount between', 'error')
            } else if(this.amount <= 0) {
                swal.fire('Please choose a valid amount', 'cmon... why would you send less than $1?', 'error')
            } else {
                const { value: question } = await swal.fire({
                    icon: 'question',
                    title: 'Are you sure?',
                    text: `You are about to request a $${this.amount} ${this.chosenLoan.name} loan in ${this.payments} payments. The final amount including interest is
                    $${parseFloat(this.amount) + parseFloat(this.amount)*0.2} ($${(parseFloat(this.amount) + parseFloat(this.amount)*0.2)/this.payments} per month)`,
                    showCancelButton: true
                })
                console.log(question)
                if(question) {
                    console.log('listo')
                    axios.post('http://localhost:8080/api/clients/current/loans', 
                    {
                        id: this.chosenLoan.id, 
                        amount: this.amount, 
                        payments: this.payments, 
                        destinyAccountNumber: this.destinyAccount
                    })
                    .then(response => {
                        console.log(response);
                        swal.fire('Done!', 'Your loan has been accepted', 'success')
                        
                    })
                    .catch(err => swal.fire('Account not found', 'try sending money to an existing account...', 'error')) 
                }
            }

        }
    },
    computed: {
        findLoan() {
            this.chosenLoan = this.loans.find(loan => loan.name == this.chosenLoanName);
            console.log(this.chosenLoan)
        }
    }

}).mount('#app')