import {prepareProductForEdit} from 'services/Products';

const initialState = {
  OTADevices: {
    loading: false,
    data: [],
    status: null,
    selectedDevicesIds: [],
    firmwareUploadInfo: {
      uploadPercent: 0,
      status: -1, // -1 - didn't touch, 0 - uploading 1 - success 2 - failed
      link: null,
      name: null
    },
    firmwareFetchInfo: {
      loading: false,
      data: {}
    },
    firmwareUpdate: {
      loading: false,
      status: null,
    }
  },
  creating: {
    info: {
      invalid: true,
      values: {}
    },
    metadata: {
      invalid: true,
      fields: []
    }
  },
  edit: {
    entity: {},
    info: {
      invalid: true,
      values: {}
    },
    metadata: {
      invalid: true,
      fields: []
    },
    dataStreams: {
      invalid: true,
      fields: []
    },
    events: {
      invalid: true,
      fields: []
    },
  },
  dashboardPreview: {
    isLoading: false,
    devicesList: [],
    selectedDeviceId: 0,
  },
  products: null
};

export default function Product(state = initialState, action) {
  switch (action.type) {

    case "PRODUCT_DASHBOARD_DEVICE_ID_FOR_PREVIEW_CHANGE":
      return {
        ...state,
        dashboardPreview: {
          ...state.dashboardPreview,
          selectedDeviceId: action.data
        }
      };

    case "PRODUCT_INFO_OTA_FIRMWARE_UPLOAD_UPDATE":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareUploadInfo: {
            ...state.OTADevices.firmwareUploadInfo,
            ...action.data
          }
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_START":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareUpdate: {
            ...state.OTADevices.firmwareUpdate,
            loading: true,
            status: null
          }
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_START_SUCCESS":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareUpdate: {
            ...state.OTADevices.firmwareUpdate,
            loading: false,
            status: true,
          }
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_START_FAILURE":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareUpdate: {
            ...state.OTADevices.firmwareUpdate,
            loading: false,
            status: false
          }
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_FIRMWARE_INFO_FETCH":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareFetchInfo: {
            ...state.OTADevices.firmwareFetchInfo,
            loading: true,
            data: {}
          }
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_FIRMWARE_INFO_FETCH_SUCCESS":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          firmwareFetchInfo: {
            ...state.OTADevices.firmwareFetchInfo,
            loading: false,
            data: {
              ...action.payload.data
            }
          }
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH":
      return {
        ...state,
        dashboardPreview: {
          ...state.dashboardPreview,
          isLoading: true,
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH_SUCCESS":
      return {
        ...state,
        dashboardPreview: {
          ...state.dashboardPreview,
          devicesList: action.payload.data.filter((device) => Number(device.productId) === Number(action.meta.previousAction.productId)),
          isLoading: false,
        }
      };

    case "API_DEVICES_LIST_FOR_PRODUCT_DASHBOARD_PREVIEW_FETCH_FAILURE":
      return {
        ...state,
        dashboardPreview: {
          ...state.dashboardPreview,
          isLoading: false,
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_FETCH":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          loading: true,
          status: null,
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_FETCH_SUCCESS":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          loading: false,
          data: action.payload.data,
          status: true,
        }
      };

    case "PRODUCT_INFO_DEVICES_OTA_FAILURE":
      return {
        ...state,
        OTADevices: {
          ...state.OTADevices,
          loading: false,
          status: false
        }
      };

    case "PRODUCT_EDIT_METADATA_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_EVENTS_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          events: {
            ...state.edit.events,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_DATA_STREAMS_FIELDS_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dataStreams: {
            ...state.edit.dataStreams,
            fields: action.data
          }
        }
      };

    case "PRODUCT_EDIT_METADATA_FIELD_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: state.edit.metadata.fields.map((field) => field.id !== action.data.id ? field : action.data)
          }
        }
      };

    case "PRODUCT_EDIT_DATA_STREAMS_FIELD_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          dataStreams: {
            ...state.edit.dataStreams,
            fields: state.edit.dataStreams.fields.map((field) => field.id !== action.data.id ? field : action.data)
          }
        }
      };

    case "API_PRODUCT_DELETE_SUCCESS":
      return {
        ...state
      };

    case "PRODUCT_SET_EDIT":
      return {
        ...state,
        edit: {
          ...state.edit,
          entity: action.data, // to check with original entity meta/event/datastreams ids if something was delete/create
          ...prepareProductForEdit(action.data)
        }
      };

    case "API_PRODUCTS_FETCH_SUCCESS":
      return {
        ...state,
        products: action.payload.data
      };

    case "API_PRODUCT_FETCH_SUCCESS":

      let products = (state.products || []).filter((product) => product.id !== action.payload.data.id);

      products = products.concat(action.payload.data);

      return {
        ...state,
        products: products
      };

    case "PRODUCT_METADATA_FIELD_ADD":
      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: state.creating.metadata.fields.concat({
              invalid: false,
              ...action.data,
              id: state.creating.metadata.fields.reduce((acc, value) => (
                acc < value.id ? value.id : acc
              ), state.creating.metadata.fields.length ? state.creating.metadata.fields[0].id : 0) + 1,
            })
          }
        }
      };

    case "PRODUCT_INFO_UPDATE_INVALID_FLAG":
      return {
        ...state,
        edit: {
          ...state.edit,
          info: {
            ...state.edit.info,
            invalid: action.data
          }
        }
      };

    case "PRODUCT_METADATA_UPDATE_INVALID_FLAG":
      return {
        ...state,
        metadata: {
          ...state.metadata,
          invalid: action.data
        }
      };

    case "PRODUCT_EDIT_CLEAR_FIELDS":
      return {
        ...state,
        edit: {
          info: {
            invalid: true,
            values: {}
          },
          metadata: {
            invalid: true,
            fields: []
          },
          dataStreams: {
            invalid: true,
            fields: []
          }
        }
      };

    case "PRODUCT_EDIT_INFO_VALUES_UPDATE":
      return {
        ...state,
        edit: {
          ...state.edit,
          info: {
            ...state.edit.info,
            values: {
              ...state.edit.info.values,
              ...action.data
            }
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_DELETE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...state.creating.metadata.fields.filter((i) => i.id !== action.data.id)
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELDS_ORDER_UPDATE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...action.data
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_VALUES_UPDATE":

      return {
        ...state,
        creating: {
          ...state.creating,
          metadata: {
            ...state.creating.metadata,
            fields: [
              ...state.creating.metadata.fields.map((field) => {
                if (field.id === action.data.id) {
                  return Object.assign({}, field, {
                    values: action.data.values
                  });
                }
                return field;
              })
            ]
          }
        }
      };

    case "PRODUCT_METADATA_FIELD_INVALID_FLAG_UPDATE":

      return {
        ...state,
        edit: {
          ...state.edit,
          metadata: {
            ...state.edit.metadata,
            fields: [
              ...state.edit.metadata.fields.map((field) => {
                if (field.id === action.data.id) {
                  return Object.assign({}, field, {
                    invalid: action.data.invalid
                  });
                }
                return field;
              })
            ]
          }
        }
      };
    case "PRODUCT_INFO_OTA_DEVICES_SELECTED_DEVICES_UPDATE":
      return {
        ...state,
        OTADevices:{
          ...state.OTADevices,
          selectedDevicesIds : action.data
        }
      };

    default:
      return state;
  }
}
