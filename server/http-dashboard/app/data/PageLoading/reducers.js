const initialState = {
  isPageLoading: false
};

export default function PageLoading(state = initialState, action) {

  switch (action.type) {
    case "START_PAGE_LOADING":
      return {
        isPageLoading: true
      };

    case "FINISH_PAGE_LOADING":
      return {
        isPageLoading: false
      };

    default:
      return state;
  }

}
