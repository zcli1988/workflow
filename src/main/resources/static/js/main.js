const app = new Vue({
    el: '#app',
    data() {
        return {
            processes: [],
            tasks: []
        }
    },
    computed: {},
    methods: {
        startProcess(processId) {
            axios
                .post('/activiti/process/start/' + processId)
                .then(response => {
                    this.tasks = response.data
                })
                .catch(error => console.log(error))
        },
        completeTask(taskId) {
            axios
                .post('/activiti/task/complete/' + taskId)
                .then(response => {
                    this.tasks = response.data
                })
                .catch(error => console.log(error))
        }
    },
    mounted() {
        axios
            .get('/activiti/processes')
            .then(response => {
                this.processes = response.data
            })
            .catch(error => console.log(error))
        axios
            .get('/activiti/tasks')
            .then(response => {
                this.tasks = response.data
            })
            .catch(error => console.log(error))
    },
    //每次页面渲染完之后滚动条在最底部
    updated() {
        this.$nextTick(function () {
            const div = document.getElementById('friendMessage');
            if (div != null) {
                div.scrollTop = div.scrollHeight;
            }
        })
    }
});