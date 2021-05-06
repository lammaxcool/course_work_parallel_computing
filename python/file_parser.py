from zipfile import ZipFile, Path
import threading


class threadCopy(threading.Thread):
    def __init__(self, group=None, target=None, name=None,
                 *args, **kwargs):
        # call constructor for Thread
        super(threadCopy,self).__init__(group=group, target=target, 
                            name=name)

        self.zip_path = kwargs.get('zip_path')
        self.path = kwargs.get('path')
        self.start_index = kwargs.get('start_index')
        self.end_index = kwargs.get('end_index')
        self.dest_path = kwargs.get('dest_path')
        self.prefix = '_'.join(self.path[:-1].split('/')[-2:])

    def run(self):
        # open zip file
        zip_obj = ZipFile(self.zip_path, 'r')
        root = Path(zip_obj, at=self.path)
        
        print(f'Processing {root}\n', end='')

        # sort files and get needed
        # according to start and end indices
        files_to_cp = sorted(list(root.iterdir()), 
                             key=lambda x: int(x.name.split('_')[0]))[self.start_index : self.end_index]

        # read needed files and 
        # write it to new files 
        for file in files_to_cp:
            curr_path = self.path + file.name
            with open(self.dest_path + self.prefix + '_' + file.name, 'wb') as dest_file:
                with zip_obj.open(curr_path, 'r') as origin_file:
                    dest_file.write(origin_file.read())

        # close zip file
        zip_obj.close()


if __name__ == '__main__':

    #  variant
    V = 36
    # indices files to retrive
    N_list = [12500]*4 + [50000]
    N_list = [(int(N/50*(V-1)), int(N/50*V)) for N in N_list]
    # folders to retrive files
    path_to_execute = ['datasets/aclImdb/test/neg/', 
                       'datasets/aclImdb/test/pos/', 
                       'datasets/aclImdb/train/neg/', 
                       'datasets/aclImdb/train/pos/', 
                       'datasets/aclImdb/train/unsup/']
    zip_path = '../datasets.zip'
    dest_path = '../data/'


    # print indices for files to get
    print(N_list)

    # start copying files to dest
    threads = []
    for path, indices in zip(path_to_execute, N_list): 
        threads.append(threadCopy(zip_path=zip_path, dest_path=dest_path, path=path, start_index=indices[0], end_index=indices[1]))
        threads[-1].start()

    # wait for threads 
    for thread in threads:
        thread.join()

    print('Job done')