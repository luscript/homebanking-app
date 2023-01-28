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
                    .then(res => Swal.fire('We sent you an email with a token to recover your password'))
                    .catch(err => console.log(err))
            }
        }
    }
}).mount('#app')