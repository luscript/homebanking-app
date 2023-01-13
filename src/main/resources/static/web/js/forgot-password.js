const { createApp } = Vue;


createApp({
    data() {
        return {
            email: ''
        }
    },
    created() {

    },
    methods: {
        verifyMail() {
            if (!this.email) {
                console.log('email cannot be empty')
            } else {
                axios.post('http://localhost:8080/api/password-token', `email=${this.email}`)
                    .then(res => console.log(res))
                    .catch(err => console.log(err))
            }
        }
    }
}).mount('#app')