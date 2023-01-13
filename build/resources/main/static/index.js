const { createApp } = Vue;

createApp({
    data() {
        return {
            data: {},
            clients: [],
            firstName: '',
            lastName: '',
            email: '',
            client: {}
        }
    },
    created() {
        this.loadData();
    },
    methods: {
        loadData() {
            axios.get('http://localhost:8080/api/clients')
                .then(response => {
                    console.log(response)
                    this.data = response.data;
                    this.clients = response.data;   
                })
                .catch(error => console.log(error))
        },
        addClient() {
            if (this.email.toLowerCase().includes('.com')) {
                this.client = {
                    firstName: this.firstName, lastName: this.lastName, email: this.email
                }
                this.postClient(this.client);
            }
        },
        deleteClient(client) {
            let id = client.id;
            console.log(id)

            if (client.accounts.length) {
                client.accounts.forEach(account => {
                    let accountid = account.id;
                    axios.delete(`http://localhost:8080/rest/accounts/${accountid}`)
                        .then(response => this.loadData())
                        .catch(err => console.log('No se pudo borrar la cuenta'))
                })
            }  
            axios.delete(`http://localhost:8080/rest/clients/${id}`)
                .then(data => this.loadData()) 
                .catch(err => console.log('no se pudo borrar el cliente')) 
        },
        postClient(client) {
            console.log(this.client)
            axios.post('http://localhost:8080/rest/clients/')
                .then(data => this.loadData())
        },
        async modifyClient(client) {
            const { value: formValues } = await Swal.fire({
                title: 'Modify client information',
                html:
                    `<input id="swal-input1" class="swal2-input" value="${client.firstName}">` +
                    `<input id="swal-input2" class="swal2-input" value="${client.lastName}">` +
                    `<input id="swal-input3" class="swal2-input" value="${client.email}">`,
                focusConfirm: false,
                preConfirm: () => {
                  return [
                        document.getElementById('swal-input1').value,
                        document.getElementById('swal-input2').value,
                        document.getElementById('swal-input3').value   
                    ]
                }
              })
              
            if (formValues[0] && formValues[1] && formValues[2].includes('@') && formValues[2].includes('.')) {
                console.log(client)
                let id = client.id;
                console.log(id)
                client = { firstName: formValues[0], lastName: formValues[1], email: formValues[2] }
                
                Swal.fire(
                    'The client has been modified succesfully',
                    '',
                    'success'
                )
                axios.put(`http://localhost:8080/rest/clients/${id}`, client)
                    .then(response => {
                        console.log(client);
                        this.loadData();
                    })
            } else if (!formValues[0] || !formValues[1] || !formValues[2]) {
                Swal.fire({
                    icon: 'error',
                    title: "Error",
                    text: 'Input values must not be empty'
                })
            } else {
                Swal.fire({
                    icon: 'error',
                    title: "Error",
                    text: 'email must contain @ and .'
                })
            }
        },
        async modifyProperty(client, property, value) {
            const { value: formValues } = await Swal.fire({
                title: property,
                html:
                  `<input id="swal-input1" class="swal2-input" value="${value}">`,
                focusConfirm: false,
                preConfirm: () => {
                  return [
                    document.getElementById('swal-input1').value
                  ]
                }
            })
            let id = client.id;
                let aux = {};
            const update = () => {
                axios.patch(`http://localhost:8080/rest/clients/${id}`, aux)
                    .then(response => this.loadData())
                    .catch(err => console.log(err))
            }
            if (formValues) {
                switch (property) {
                    case 'First name':
                        aux['firstName'] = formValues[0];
                        update();
                        break;
                    case 'Last name':
                        aux['lastName'] = formValues[0];
                        update();
                        break;
                    case 'Email':
                        if (formValues[0].includes('.') && formValues[0].includes('@')) {
                            aux['email'] = formValues[0];
                            update();
                        } else {
                            Swal.fire({
                                icon: 'error',
                                title: "Error",
                                text: 'email must contain @ and .'
                            })
                        }
                        break;
                }
                      
            }
            
        }
    }
}).mount('#app')