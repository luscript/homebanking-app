const { createApp } = Vue;

createApp({
    data() {
        return {
            client: {},
            cards: [],
            checked: undefined,
            debitCards: [],
            creditCards: [],
            type: undefined,
            color: undefined
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
                    this.cards = data.data.cards;
                    console.log(this.cards)
                    this.debitCards = this.cards.filter((card) => card.type === "DEBIT")
                    this.creditCards = this.cards.filter((card) => card.type === "CREDIT")
                })
                .catch(err => console.log(err))
        },
        click() {
            axios.post('/api/logout').then(response => window.location.href = "http://localhost:8080/web/index.html")
        },
        async createCard() {
            if(!this.type || !this.color) {
                swal.fire(`Please fill in all the data`, ">:C", 'error')
            } else {
                const { value: question } = await swal.fire({
                    icon: 'question',
                    title: 'Are you sure?',
                    text: `You are about to create a ${(this.type).toLowerCase()} ${(this.color).toLowerCase()} card`,
                    showCancelButton: true
                })
                if(question) {
                    axios.post('http://localhost:8080/api/clients/current/cards', `type=${this.type}&color=${this.color}`)
                    .then(response => this.loadData())
                    .catch(err => swal.fire(`You already have a ${this.type} ${this.color} card`, "we're sorry :P", 'error'))
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