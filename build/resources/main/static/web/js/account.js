const { createApp } = Vue;
const { jsPDF } = window.jspdf;


let myTable = document.getElementById('table');

createApp({
    data() {
        return {
            account: {},
            transactions: [],
            checked: undefined,
            client: {},
            today: '',
            selectedFilter: '7',
            date: undefined
        }
    },
    created() {
        this.checked = JSON.parse(localStorage.getItem("checked"))
        const urlParams = new URLSearchParams(window.location.search);
        const id = urlParams.get('id');
        console.log(window)
        this.loadData(id);

    },
    methods: {
        loadData(id) {
            this.today = new Date(new Date().setDate(new Date().getDate()))
            this.today = this.today.toISOString();
            this.today = this.today.slice(0, -1);
            console.log(this.today)
            this.today = this.today.replace('T', ' ');
            console.log(this.today)
            let lastsevendays = new Date(new Date().setDate(new Date().getDate() - 7))
            lastsevendays = lastsevendays.toISOString();
            /* axios.get(`http://localhost:8080/api/accounts/${id}`, {
                params: {
                    from_date: lastsevendays,
                    thru_date: this.today
                }
            })
                .then(accountData => {
                    this.account = accountData.data;
                    this.transactions = this.account.transactions;
                    console.log(this.account)
                })
                .catch(err => console.log(err)) */
        },
        tableRowStyle(transactionType) {
            let style = {};
            if (transactionType === 'CREDIT') {
                style.backgroundColor = '#c1fba4';
            } else {
                style.backgroundColor = '#ff928b';
            }
            return style;
        },
        click() {
            axios.post('/api/logout').then(response => window.location.href = "http://localhost:8080/web/index.html")
        },
        downloadPDF() {
            axios({
                method: 'post',
                url: 'http://localhost:8080/api/download-pdf',
                responseType: 'blob', // important,
                headers: { 'Content-Type': 'application/json' },
                data: JSON.stringify({transactions: this.transactions})
              }).then((response) => {
                const url = window.URL.createObjectURL(new Blob([response.data]));
                const link = document.createElement('a');
                link.href = url;
                link.setAttribute('download', 'table.pdf'); //or any other extension
                document.body.appendChild(link);
                link.click();
              }).catch((error) => {
                console.log(error);
              });
        }
        /* downloadPDF() {
        
            var headers = ["Amount", "Date", "Description", "Remaining balance", "Type"]
            var rows = []
            this.transactions.forEach(transaction => {
                var row = []
                for (prop in transaction) {
                    if (prop == 'id') continue;
                    if(prop == 'amount' || prop == 'remainingBalance') {
                        let stringProp = transaction[prop].toString();
                        row.push(stringProp);
                    } else {
                        row.push(transaction[prop]);
                    }
                    
                }
                rows.push(row);
            })
            console.log([rows]) 

            var headers = ["Amount", "Date", "Description", "Remaining balance", "Type"]
            var doc = new jsPDF()
            doc.autoTable({
                startY: 20,
                head: [headers],
                body: rows
            })
            doc.save('table.pdf') 
            
        } */
    },
    computed: {
        filterTransactions() {

            let lastxdays;

            switch (this.selectedFilter) {
                case '7':
                    lastxdays = new Date(new Date().setDate(new Date().getDate() - 7))
                    break;
                case '15':
                    lastxdays = new Date(new Date().setDate(new Date().getDate() - 15))
                    break;
                case '30':
                    lastxdays = new Date(new Date().setDate(new Date().getDate() - 30))
                    break;
                case '365':
                    lastxdays = new Date(new Date().setDate(new Date().getDate() - 365))
                    break;
                case 'all':
                    lastxdays = new Date((2022, 11, 01, 0, 0, 0, 0));
                    break;
            }
            lastxdays = lastxdays.toISOString().slice(0, -1);
            lastxdays = lastxdays.replace('T', ' ');
            const urlParams = new URLSearchParams(window.location.search);
            const id = urlParams.get('id');
            if (lastxdays == 0) {
                lastxdays = '';
                this.thru_date = '';
            }
            axios.get(`http://localhost:8080/api/accounts/${id}`, {
                params: {
                    from_date: lastxdays + '000',
                    thru_date: this.today + '000'
                }
            })
                .then(accountData => {
                    console.log(accountData)
                    this.account = accountData.data;
                    this.transactions = this.account.transactions;
                    this.transactions = this.account.transactions.sort((a, b) => a.date - b.date)
                })
                .catch(err => console.log(err))


        },
        filterByExactDate() {
            if (this.date) {
                let from_date = this.date + ' 00:00:00.398000'
                let thru_date = this.date + ' 23:59:59.000000'

                const urlParams = new URLSearchParams(window.location.search);
                const id = urlParams.get('id');

                axios.get(`http://localhost:8080/api/accounts/${id}`, {
                    params: {
                        from_date,
                        thru_date
                    }
                })
                    .then(accountData => {
                        console.log(accountData)
                        this.account = accountData.data;
                        this.transactions = this.account.transactions;
                        this.transactions = this.account.transactions.sort((a, b) => a.date - b.date)
                    })
                    .catch(err => console.log(err))

            }
        }
    }
}).mount('#app')