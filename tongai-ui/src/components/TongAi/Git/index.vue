<template>
  <div>
    <svg-icon icon-class="github" @click="goto" />
  </div>
</template>

<script>
export default {
  name: 'TongAiGit',
  data() {
    return {
      url: 'https://gitee.com/tongailinc/TongAi.git'
    }
  },
  methods: {
    goto() {
      window.open(this.url)
    }
  }
}
</script>
