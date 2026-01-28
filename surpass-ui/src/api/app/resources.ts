import request from "../../utils/Request.js";

export function create(data : any): any {
    return request({
        url: '/app-resources/add',
        method: 'post',
        data: data
    })
}

export function update(data : any): any {
    return request({
        url: '/app-resources/update',
        method: 'put',
        data: data
    })
}

export function pageResources(params: any): any {
    return request({
        url: '/app-resources/page',
        method: 'get',
        params: params
    })
}

export function deleteData(id : any): any {
    return request({
        url: `/app-resources/delete?ids=` + id,
        method: 'delete'
    })
}

export function getTree(query?: any): any {
    return request({
        url: '/app-resources/tree',
        method: 'get',
        params: query
    })
}

export function getTreeClient(query?: any): any {
    return request({
        url: '/app-resources/tree/client',
        method: 'get',
        params: query
    })
}

export function saveClientAuthz(data: any) {
    return request({
        url: '/app-resources/clientAuthz',
        method: 'post',
        data: data
    })
}

export function getResourceByClient(query: any): any {
    return request({
        url: '/app-resources/getClientAuthz',
        method: 'get',
        params: query
    })
}
