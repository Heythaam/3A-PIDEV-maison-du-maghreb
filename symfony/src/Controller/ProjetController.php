<?php

namespace App\Controller;
use App\Entity\Collaboration;
use App\Entity\Projet;
use App\Form\ProjetType;
use App\Repository\CollaborationRepository;
use Symfony\Bundle\FrameworkBundle\Controller\AbstractController;
use Symfony\Component\String\Slugger\SluggerInterface;
use Doctrine\ORM\EntityManagerInterface;
use Symfony\Component\HttpFoundation\Request;
use Symfony\Component\HttpFoundation\File\Exception\FileException;
use Symfony\Component\HttpFoundation\Response;
use Symfony\Component\Routing\Attribute\Route;


final class ProjetController extends AbstractController{
    private $entityManager;

    public function __construct(EntityManagerInterface $entityManager)
    {
        $this->entityManager = $entityManager;
    }
    #[Route('/projet', name: 'app_projet')]
    public function index(): Response
    {
        return $this->render('projet/index.html.twig', [
            'controller_name' => 'ProjetController',
        ]);
    }
    #[Route('/projet/new/{collabId}', name: 'app_ajoutProjet')]
    public function new(int $collabId, Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger)
    {
        $collaboration = $entityManager->getRepository(Collaboration::class)->find($collabId);
        if (!$collaboration) {
            throw $this->createNotFoundException('Collaboration not found');
        }

        $project = new Projet();
        $project->setCollaboration($collaboration);
        $currentDate = new \DateTime(); 
        $currentDate->setTime(0, 0, 0);

        if ($project->getStartDate() > $currentDate) {
            $status = "A venir"; 
        } elseif ($project->getEndDate() < $currentDate) {
            $status = "Terminé"; 
        } else {
            $status = "Active";
        }
        dump($project->getStartDate(), $project->getEndDate(), $currentDate);
        $project->setStatus($status);

        $form = $this->createForm(ProjetType::class, $project);
        $form->handleRequest($request);

        if ($form->isSubmitted() && $form->isValid()) {
            $project->setStartDate($form->get('startDate')->getData());
            $project->setEndDate($form->get('endDate')->getData());
            $imageFile = $form->get('image')->getData();
    
            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();
    
                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'), 
                        $newFilename
                    );
                } catch (FileException $e) {
                    throw new \Exception('Error uploading file: ' . $e->getMessage());
                }
    
               
                $project->setImage($newFilename);
            }
            $entityManager->persist($project);
            $entityManager->flush();

            return $this->redirectToRoute('collaboration_details', ['id' => $collabId]);
        }

        return $this->render('projet/ajoutProjet.html.twig', [
            'form' => $form->createView(),
        ]);
    }
    #[Route('/projet/new/', name: 'app_ajoutProjetBackOffice')]
    public function newBack(Request $request, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $project = new Projet();
        $currentDate = new \DateTime(); 
        $currentDate->setTime(0, 0, 0);

        $status = "Active"; // Default status
        $project->setStatus($status);

        $form = $this->createForm(ProjetType::class, $project);
        $form->handleRequest($request);

        if ($form->isSubmitted()) {
            $imageFile = $form->get('image')->getData();

            // Check if image file is present
            if (!$imageFile) {
                $this->addFlash('danger', 'Please select an image file.');
                return $this->render('projet/ajoutProjetBack.html.twig', [
                    'form' => $form->createView(),
                ]);
            }

            if ($form->isValid()) {
                try {
                    $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                    $safeFilename = $slugger->slug($originalFilename);
                    $newFilename = $safeFilename.'-'.uniqid().'.'.$imageFile->guessExtension();

                    // Move the file to the directory where images are stored
                    $imageFile->move(
                        $this->getParameter('uploads_directory'),
                        $newFilename
                    );

                    // Update the project with the new image filename
                    $project->setImage($newFilename);
                    
                    $entityManager->persist($project);
                    $entityManager->flush();

                    $this->addFlash('success', 'Project added successfully.');
                    return $this->redirectToRoute('app_collaboration');
                } catch (FileException $e) {
                    $this->addFlash('danger', 'Error uploading file: ' . $e->getMessage());
                }
            } else {
                $this->addFlash('danger', 'Please correct the errors in the form.');
            }
        }

        return $this->render('projet/ajoutProjetBack.html.twig', [
            'form' => $form->createView(),
        ]);
    }
    
    #[Route('/projet/details/{id}', name: 'projet_details')]
    public function show(Projet $projet): Response
    {
        $collaboration = $projet->getCollaboration();
        return $this->render('projet/projetDetails.html.twig', [
            'projet' => $projet,
            'collaboration' => $collaboration,
        ]);
    }
    #[Route("/projet/delete/{id}", name: "delete_projet", methods:("POST"))]
    public function delete(Request $request, Projet $projet): Response
    {
        $collaboration = $projet->getCollaboration();

        if ($this->isCsrfTokenValid('delete'.$projet->getId(), $request->request->get('_token'))) {
            $this->entityManager->remove($projet);
            $this->entityManager->flush();
        }

        return $this->redirectToRoute('collaboration_details', ['id' => $collaboration->getId()]);
    }
    #[Route('/projet/edit/{id}', name:'app_editProjet')]
    public function edit(Request $request, Projet $projet, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $form = $this->createForm(ProjetType::class, $projet);
        $form->handleRequest($request);
        $currentDate = new \DateTime(); 

        if ($projet->getStartDate() > $currentDate) {
            $status = "A venir"; 
        } elseif ($projet->getEndDate() < $currentDate) {
            $status = "Terminé"; 
        } else {
            $status = "Active";
        }
        $projet->setStatus($status);

        if ($form->isSubmitted() && $form->isValid()) {
            $projet->setStartDate($form->get('startDate')->getData());
            $projet->setEndDate($form->get('endDate')->getData());
            $imageFile = $form->get('image')->getData();

            if ($imageFile) {
                $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                $safeFilename = $slugger->slug($originalFilename);
                $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                try {
                    $imageFile->move(
                        $this->getParameter('uploads_directory'), 
                        $newFilename
                    );
                } catch (FileException $e) {
                    throw new \Exception('Error uploading file: ' . $e->getMessage());
                }

                $projet->setImage($newFilename);
            }

            $entityManager->persist($projet);
            $entityManager->flush();

            return $this->redirectToRoute('projet_details', ['id' => $projet->getId()]);
        }

        return $this->render('projet/editProjet.html.twig', [
            'form' => $form->createView(),
        ]);
    }
    #[Route('/projet/editBack/{id}', name:'app_editProjetBack')]
    public function editBack(Request $request, Projet $projet, EntityManagerInterface $entityManager, SluggerInterface $slugger): Response
    {
        $form = $this->createForm(ProjetType::class, $projet);
        $form->handleRequest($request);
        
        // Add debugging
        if ($form->isSubmitted()) {
            $imageFile = $form->get('image')->getData();
            dump([
                'isSubmitted' => true,
                'isValid' => $form->isValid(),
                'imageFile' => $imageFile,
                'errors' => $form->getErrors(true),
                'uploadDirectory' => $this->getParameter('uploads_directory')
            ]);

            if ($form->isValid()) {
                try {
                    if ($imageFile) {
                        // Check if upload directory exists and is writable
                        $uploadDir = $this->getParameter('uploads_directory');
                        if (!is_dir($uploadDir)) {
                            mkdir($uploadDir, 0777, true);
                        }

                        if (!is_writable($uploadDir)) {
                            throw new \RuntimeException('Upload directory is not writable');
                        }

                        $originalFilename = pathinfo($imageFile->getClientOriginalName(), PATHINFO_FILENAME);
                        $safeFilename = $slugger->slug($originalFilename);
                        $newFilename = $safeFilename . '-' . uniqid() . '.' . $imageFile->guessExtension();

                        // Add more debugging
                        dump([
                            'originalFilename' => $originalFilename,
                            'newFilename' => $newFilename,
                            'uploadPath' => $uploadDir . '/' . $newFilename
                        ]);

                        $imageFile->move($uploadDir, $newFilename);
                        $projet->setImage($newFilename);
                    }

                    $entityManager->persist($projet);
                    $entityManager->flush();

                    $this->addFlash('success', 'Projet modifié avec succès.');
                    return $this->redirectToRoute('app_collaboration');
                } catch (\Exception $e) {
                    // Add detailed error message
                    $this->addFlash('danger', 'Error: ' . $e->getMessage());
                    dump($e);
                }
            } else {
                $this->addFlash('danger', 'Erreur : Veuillez corriger les erreurs du formulaire.');
                dump($form->getErrors(true));
            }
        }

        return $this->render('projet/editProjetBack.html.twig', [
            'form' => $form->createView(),
            'projet' => $projet
        ]);
    }




}
