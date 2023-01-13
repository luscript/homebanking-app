const { createApp } = Vue;


createApp({
    data() {
        return {
            password: '',
            confirmPassword: '';
        }
    },
    created() {

    },
    methods: {
        resetPassword() {
            if (this.password != this.confirmPassword) {
                console.log('passwords do not match')
            } else {
                axios.post('http://localhost:8080/api/reset-password', `password=${this.password}`)
                    .then(res => console.log(res))
                    .catch(err => console.log(err))
            }
        }
    }
}).mount('#app')