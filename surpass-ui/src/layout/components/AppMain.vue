<template>
  <section class="app-main">
    <div class="main-navbar">
      <Breadcrumb id="breadcrumb-container" class="breadcrumb-container"/>
      <!--<div class="page-title">{{ route.meta.title }}</div>-->
    </div>

    <router-view v-slot="{ Component, route }">
      <transition name="fade-transform" mode="out-in">
        <!--        <keep-alive :include="tagsViewStore.cachedViews">-->
        <!--        </keep-alive>-->
        <component v-if="!route.meta.link" :is="Component" :key="route.path"/>
      </transition>
    </router-view>
    <iframe-toggle/>
  </section>
  <Footer position="relative"></Footer>
</template>

<script setup>
import iframeToggle from "./IframeToggle/index"
import useTagsViewStore from '@/store/modules/tagsView'
import Breadcrumb from "@/components/Breadcrumb/index.vue";
import Footer from "@/components/Footer/index.vue";

const route = useRoute();
const tagsViewStore = useTagsViewStore()
</script>

<style lang="scss" scoped>
@import "@/assets/styles/variables.module";

.app-main {
  background-color: #f5f7fa;
  height: calc(100vh - #{$base-navbar-height});
  width: 100%;
  position: relative;
  overflow: auto;

  .main-navbar {
    padding: 0 20px;
    background-color: #FFFFFF;
    border-bottom: 1px solid #d8dce5;
    //box-shadow: 0 1px 3px 0 rgba(0, 0, 0, .12), 0 0 3px 0 rgba(0, 0, 0, .04);

    .breadcrumb-container {
      margin-top: 5px;
      line-height: 40px;
    }

    .page-title {
      font-size: 20px;
      margin-bottom: 15px;
    }
  }
}

.fixed-header + .app-main {
  padding-top: 50px;
}

.hasTagsView {
  .app-main {
    /* 84 = navbar + tags-view = 50 + 34 */
    min-height: calc(100vh - 84px);
  }

  .fixed-header + .app-main {
    padding-top: 84px;
  }
}
</style>

<style lang="scss">
// fix css style bug in open el-dialog
.el-popup-parent--hidden {
  .fixed-header {
    padding-right: 6px;
  }
}

::-webkit-scrollbar {
  width: 6px;
  height: 6px;
}

::-webkit-scrollbar-track {
  background-color: #f1f1f1;
}

::-webkit-scrollbar-thumb {
  background-color: #c0c0c0;
  border-radius: 3px;
}
</style>

