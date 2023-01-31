const { createApp } = Vue;

createApp({
    data() {
        return {
            client: {},
            payments: [6, 12, 18, 24, 30, 36, 42, 48],
            selectedPayments: [],
            loanName: ''
        }
    },
    created() {
        this.loadData();
    },
    methods: {
        loadData() {
            axios.get('/api/clients/current')
                .then(data => {
                    this.client = data.data;
                    console.log(this.client)

                })
                .catch(err => console.log(err))
        },
        async createLoan() {
            if (!this.selectedPayments.length || !this.loanName) {
                Swal.fire('Please fill in all the data', ':c', 'error')
            } else {
                const { value: question } = await swal.fire({
                    icon: 'question',
                    title: 'Are you sure?',
                    text: `You are about to create a ${this.loanName} loan with the following payments: ${this.selectedPayments.join(',')}`,
                    showCancelButton: true
                })
                if (question) {
                    axios.post('/api/create-loan', `payments=${this.selectedPayments.join(',')}&name=${this.loanName}`)
                        .then(res => Swal.fire('Yikes!', 'Now we are going to encourage people to become indebted!', 'success'))
                        .catch(err => {
                            if(err.response.data == 'loan already exists') {
                                Swal.fire('That type of loan already exists', ':c', 'error')
                            } else {
                                Swal.fire('Error', 'I dunno what happened but I could not process your request', 'error')
                            }
                        })
                }
            }
        }
    }
}).mount('#app')