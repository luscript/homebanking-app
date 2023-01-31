const { createApp } = Vue;

let url = window.location.search;
let myurl = new URLSearchParams(url)
let finalUrl = myurl.get("token")


createApp({
    data() {

    },
    created() {
        console.log(`token=${finalUrl}`)
        axios.post('/api/confirm-account', `token=${finalUrl}`)
            .then(res => console.log(res))
            .catch(err => console.log(err))
    }
}).mount('#app')